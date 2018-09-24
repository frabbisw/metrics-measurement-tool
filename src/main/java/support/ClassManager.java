package support;

import calc_loc.LineCounter;
import class_metrics.CohesionGraph;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import method_metrics.CyclomaticComplexityCalculator;

import java.util.*;

public class ClassManager {
    ClassOrInterfaceDeclaration myClass;
    Map<String, String> classesMap;
    String myPackageName;
    ArrayList<MethodManager> methodManagers;
    Set<String> globalSet;
    LineCounter lineCounter;
    double lackOfCohesion;
    double coupling;
    double numberOfChildren;
    double levelOfInheritence=0;

    public ClassManager(String myPackageName, ClassOrInterfaceDeclaration myClass, Map<String, String> classesMap) {
        this.myPackageName = myPackageName;
        this.myClass = myClass;
        this.classesMap = classesMap;
        this.methodManagers = new ArrayList<>();
        this.globalSet = new HashSet<>();
        this.lineCounter=new LineCounter(myClass);

        prepareFields();
        generateCohesionGraph();
    }

    private void prepareFields() {
        String classNameWithPackage = getFullName(myPackageName, myClass.getNameAsString());
        Map<String, String> globalMap = new TreeMap<>();

        for (FieldDeclaration field : myClass.getFields())
            for (VariableDeclarator variable : field.getVariables()) {
                globalSet.add(variable.getNameAsString());

                for (String flazz : classesMap.keySet())
                    if (flazz.equals(variable.getType().asString()))
                        globalMap.put(variable.getNameAsString(), variable.getType().asString());
            }


        for (String string : classesMap.keySet())
            globalMap.put(string, string);

        convertAndAddConstructors(myClass, globalMap);

        for (MethodDeclaration method : myClass.getMethods())
            methodManagers.add(new MethodManager(method, classNameWithPackage, globalMap, classesMap, globalSet));
    }

    private void convertAndAddConstructors(ClassOrInterfaceDeclaration clazz, Map<String, String> globalMap) {
        for (ConstructorDeclaration cd : clazz.getConstructors()) {
            MethodDeclaration md = new MethodDeclaration();
            md.setName(cd.getName());
            md.setParameters(cd.getParameters());
            md.setBody(cd.getBody());

            methodManagers.add(new MethodManager(md, getFullName(myPackageName, cd.getNameAsString()), globalMap, classesMap, globalSet));
        }
    }

    public void generateCohesionGraph() {
        CohesionGraph cohesionGraph = new CohesionGraph();
        for (MethodManager manager : methodManagers)
            cohesionGraph.addToMap(manager.getCohesionHandler());

        cohesionGraph.calculateDSU();
        lackOfCohesion=cohesionGraph.getDsu();
    }
    private String getFullName(String packageName, String className) {
        return packageName + "." + className;
    }

    public ArrayList<MethodManager> getMethodManagers() {
        return methodManagers;
    }
    public String getMyFullName()
    {
        return myPackageName+"."+myClass.getNameAsString();
    }

    public void setCoupling(double coupling) {
        this.coupling = coupling;
    }
    public void setNumberOfChildren(double children)
    {
        this.numberOfChildren=children;
    }
    public void setLevelOfInheritence(double levelOfInheritence)
    {
        this.levelOfInheritence=levelOfInheritence;
    }

    public ArrayList<String>getParents()
    {
        ArrayList<String>parents=new ArrayList<>();
        for(ClassOrInterfaceType type : myClass.getExtendedTypes())
        {
            if(classesMap.containsKey(type.getNameAsString()))
                parents.add(classesMap.get(type.getNameAsString()));
        }

        for(ClassOrInterfaceType type : myClass.getImplementedTypes())
        {
            if(classesMap.containsKey(type.getNameAsString()))
                parents.add(classesMap.get(type.getNameAsString()));
        }


        return parents;
    }

    //APIs...
    public double getLackOfCohesion()
    {
        return lackOfCohesion;
    }
    public double getResponseOfClass() {
        int sum = methodManagers.size();
        for (MethodManager manager : methodManagers)
            sum += manager.getCouplingHandler().getNumberOfCalledMethods();

        return sum;
    }
    public double getLineOfComments() {
        return lineCounter.getLineOfComments();
    }
    public double getNumberOfComments() {
        return lineCounter.getNumberOfComments();
    }
    public double getLineOfCodes()
    {
        return lineCounter.getLineOfCodes();
    }
    public double getNumberOfStatements()
    {
        return lineCounter.getNumberOfStatements();
    }
    public double getCoupling() {
        return coupling;
    }
    public double getWeightedMethodCount() {
        int WeightedMethodCount = 0;
        for (MethodDeclaration method : myClass.getMethods()) {
            CyclomaticComplexityCalculator c = new CyclomaticComplexityCalculator(method);
            int complexity = c.calculateComplexity();
            WeightedMethodCount += complexity;
        }
        return WeightedMethodCount;
    }

    public double getNumberOfChildren() {
        return numberOfChildren;
    }

    public double getLevelOfInheritance() {
        return levelOfInheritence;
    }
}