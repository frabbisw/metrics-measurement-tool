package support;

import class_metrics.CallGraphMetrics;
import collect_classes.ClassFinder;
import collect_classes.FileExplorer;
import com.github.javaparser.ast.CompilationUnit;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class MetricsTool {
    String projectPath;
    String outputPath;
    ArrayList<ClassManager> classManagers;
    CallGraphMetrics callGraphMetrics;
    InheritenceHandler inheritenceHandler;

    public MetricsTool(String projectPath, String outputPath) {
        classManagers = new ArrayList<>();
        callGraphMetrics = new CallGraphMetrics();

        this.projectPath = projectPath;
        this.outputPath = outputPath;

        ClassFinder.setProjectPath(projectPath);
        FileExplorer explorer = ClassFinder.getClassExplorer();

        for (String key : explorer.getCUMap().keySet()) {
            for (int i = 0; i < explorer.getCUMap().get(key).size(); i++) {
                CompilationUnit cu = explorer.getCUMap().get(key).get(i);
                CUManager manager = new CUManager(key, cu);

                for (ClassManager classManager : manager.getLocalClasses()) {
                    classManagers.add(classManager);
                }
            }
        }

        generateCallGraph();
        setCouplingValues();

        inheritenceHandler=new InheritenceHandler(classManagers);
        setInheritenceValues();
    }

    private void setInheritenceValues() {
        for(ClassManager classManager : classManagers)
        {
            classManager.setNumberOfChildren(inheritenceHandler.getNumberOfChildren(classManager.getMyFullName()));
            classManager.setLevelOfInheritence(inheritenceHandler.getLevelOfInheritence(classManager.getMyFullName()));
        }
    }

    private void setCouplingValues() {
        Map<String, Integer> couplingValues = callGraphMetrics.getCouplingValues();

        for(ClassManager manager : classManagers)
        {
            int value;
            try{
                value = couplingValues.get(manager.getMyFullName());
            }
            catch (Exception e){
                value=0;
            }
            manager.setCoupling(value);
        }
    }

    private void generateCallGraph() {
        for (ClassManager classManager : classManagers) {
            for (MethodManager methodManager : classManager.getMethodManagers())
                callGraphMetrics.addCouplingHandler(methodManager.getCouplingHandler());
        }
        callGraphMetrics.generateCouplingClass();
        callGraphMetrics.generateMethodCallGraph();
    }

    public void printCSV()
    {
        printForClass();
        printCMCOfMethods();
        //printCallGraphMetrics();
    }

    private void printCallGraphMetrics() {
        Map<String, Integer>methodHash=callGraphMetrics.getMethodHash();
        Integer [][] callGraph=callGraphMetrics.getCallGraph();

        String res="";
        for(int i=0; i<callGraph.length; i++)
        {
            for(int j=0; j<callGraph[i].length; j++)
            {
                res+=callGraph[i][j]+",";
            }
            res+="\n";
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath+"/callGraphMatrix.csv"));
            writer.write(res);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printCMCOfMethods() {
        String res = "";
        for(ClassManager classManager : classManagers)
        {
            for(MethodManager methodManager : classManager.getMethodManagers())
            {
                res+=methodManager.getFullName()+","+methodManager.getCmc()+"\n";
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath+"/methodCyclomatic.csv"));
            writer.write(res);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printForClass() {
        String res="";
        res+=("class Name with Package,");
        res+=("Number of statements, ");
        res+=("Line Of Comments, ");
        res+=("Coupling between other objects, ");
        res+=("Lack of cohesion, ");
        res+=("Response of class, ");
        res+=("Weighted method count, ");
        res+=("Number of children, ");
        res+=("Level of inheritance, ");
        res+="\n";

        for(ClassManager classManager : classManagers)
        {
            res+=(classManager.getMyFullName()+",");
            res+=(classManager.getNumberOfStatements()+",");
            res+=(classManager.getLineOfComments()+",");
            res+=(classManager.getCoupling()+",");
            res+=(classManager.getLackOfCohesion()+",");
            res+=(classManager.getResponseOfClass()+",");
            res+=(classManager.getWeightedMethodCount()+",");
            res+=(classManager.getNumberOfChildren())+",";
            res+=(classManager.getLevelOfInheritance())+",";
            res+="\n";
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath+"/classMetrics.csv"));
            writer.write(res);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}