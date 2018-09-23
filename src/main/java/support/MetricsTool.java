package support;

import class_metrics.CallGraphMetrics;
import collect_classes.ClassFinder;
import collect_classes.FileExplorer;
import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MetricsTool {
    String projectPath;
    ArrayList<ClassManager> classManagers;
    CallGraphMetrics callGraphMetrics;

    public MetricsTool(String projectPath) {
        classManagers = new ArrayList<>();
        callGraphMetrics = new CallGraphMetrics();

        this.projectPath = projectPath;
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

    public void printAll()
    {
        for(ClassManager classManager : classManagers)
        {
            System.out.println(classManager.getMyFullName());
            System.out.println(classManager.getLineOfCodes());
            System.out.println(classManager.getLineOfComments());
            System.out.println(classManager.getCoupling());
            System.out.println(classManager.getLackOfCohesion());
            System.out.println(classManager.getResponseOfClass());
            System.out.println(classManager.getWeightedMethodCount());
            System.out.println();
        }
    }
}