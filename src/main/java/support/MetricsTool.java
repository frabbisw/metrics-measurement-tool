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
    }

    private void setCouplingValues() {
        Map<String, Integer> couplingValues = callGraphMetrics.getCouplingValues();

        for(ClassManager manager : classManagers)
        {
            int value=couplingValues.get(manager.getMyFullName());
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
        String res="";
        for(ClassManager classManager : classManagers)
        {
            res+=(classManager.getMyFullName()+",");
            res+=(classManager.getLineOfCodes()+",");
            res+=(classManager.getLineOfComments()+",");
            res+=(classManager.getCoupling()+",");
            res+=(classManager.getLackOfCohesion()+",");
            res+=(classManager.getResponseOfClass()+",");
            res+=(classManager.getWeightedMethodCount());
            res+="\n";
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
            writer.write(res);
            writer.close();
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void printAll()
    {
        for(ClassManager classManager : classManagers)
        {
            System.out.print(classManager.getMyFullName()+",");
            System.out.print(classManager.getLineOfCodes()+",");
            System.out.print(classManager.getLineOfComments()+",");
            System.out.print(classManager.getCoupling()+",");
            System.out.print(classManager.getLackOfCohesion()+",");
            System.out.print(classManager.getResponseOfClass()+",");
            System.out.print(classManager.getWeightedMethodCount());
            System.out.println();
        }
    }
}