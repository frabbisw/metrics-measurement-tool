package support;

import class_metrics.CohesionGraph;
import collect_classes.FileExplorer;
import collect_classes.ClassFinder;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;

import java.io.File;
import java.util.*;

public class ClassManager {
    Map<String, String> classesMap;
    ArrayList<ClassOrInterfaceDeclaration>localClasses;
    ArrayList<MethodManager> methodManagers;
    Set<String>globalSet;
    String myPackageName="Not Declared";

    public ClassManager(String ParentPath, CompilationUnit compilationUnit) {

        FileExplorer fileExplorer = ClassFinder.getClassExplorer();
        classesMap = new TreeMap<>();
        localClasses=new ArrayList<>();
        methodManagers=new ArrayList<>();
        globalSet=new HashSet<>();

        if(compilationUnit.getPackageDeclaration().isPresent())
            myPackageName=compilationUnit.getPackageDeclaration().get().getNameAsString();

        for (ImportDeclaration immport : compilationUnit.getImports())
        {
            String importName=immport.getNameAsString();
            if(fileExplorer.getClassNamesByImportTag(importName)!=null)
            {
                for(String className : fileExplorer.getClassNamesByImportTag(importName))
                {
                    if(immport.toString().contains("*"))    classesMap.put(className, getFullName(importName, className));
                    else classesMap.put(className, importName);
                }
            }
        }
        if(fileExplorer.getClassNamesBySource(ParentPath)!=null)
        {
            for(String className : fileExplorer.getClassNamesBySource(ParentPath))
                classesMap.put(className, getFullName(myPackageName, className));
        }

        for(TypeDeclaration type : compilationUnit.getTypes())
        {
            if(compilationUnit.getClassByName(type.getNameAsString()).isPresent())
                //localClasses.add(compilationUnit.getClassByName(type.getNameAsString()).get());
                localClasses.add(type.asClassOrInterfaceDeclaration());
        }
    }
    public void prepareFields()
    {
        for(ClassOrInterfaceDeclaration clazz : localClasses)
        {
            String classNameWithPackage=getFullName(myPackageName, clazz.getNameAsString());
            Map<String, String>globalMap = new TreeMap<>();

            for (FieldDeclaration field : clazz.getFields())
                for(VariableDeclarator variable : field.getVariables())
                {
                    globalSet.add(variable.getNameAsString());

                    for(String flazz : classesMap.keySet())
                        if(flazz.equals(variable.getType().asString()))
                            globalMap.put(variable.getNameAsString(),variable.getType().asString());
                }


            for(String string : classesMap.keySet())
                globalMap.put(string, string);

            convertAndAddConstructors(clazz, globalMap);

            for(MethodDeclaration method : clazz.getMethods())
                methodManagers.add(new MethodManager(method, classNameWithPackage, globalMap, classesMap, globalSet));

            //for(MethodManager manager : methodManagers)
            //    manager.printCohesion();
        }
    }
    private void convertAndAddConstructors(ClassOrInterfaceDeclaration clazz, Map<String, String>globalMap) {
        for(ConstructorDeclaration cd : clazz.getConstructors())
        {
            MethodDeclaration md = new MethodDeclaration();
            md.setName(cd.getName());
            md.setParameters(cd.getParameters());
            md.setBody(cd.getBody());

            methodManagers.add(new MethodManager(md, getFullName(myPackageName,cd.getNameAsString()), globalMap, classesMap, globalSet));
        }
    }
    public void generateCohesionGraph()
    {
        CohesionGraph cohesionGraph = new CohesionGraph();
        for(MethodManager manager : methodManagers)
            cohesionGraph.addToMap(manager.getCohesionHandler());

        //cohesionGraph.showGraph();
        //System.out.println("\n\n");

        cohesionGraph.calculateDSU();
        //System.out.println(cohesionGraph.getDsu());
    }
    public int responseOfClass()
    {
        int sum=methodManagers.size();
        for(MethodManager manager:methodManagers)
            sum+=manager.getCouplingHandler().getNumberOfCalledMethods();

        return sum;
    }

    public ArrayList<MethodManager> getMethodManagers() {
        return methodManagers;
    }
    private String getFullName(String packageName, String className)
    {
        return packageName+"."+className;
    }
}