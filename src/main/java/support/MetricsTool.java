package support;

import class_metrics.CallGraphMetrics;
import collect_classes.ClassFinder;
import collect_classes.FileExplorer;
import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.Set;

public class MetricsTool {
    String projectPath;
    ArrayList<ClassManager>classManagers;
    CallGraphMetrics callGraphMetrics;

    public MetricsTool(String projectPath)
    {
        classManagers=new ArrayList<>();
        callGraphMetrics=new CallGraphMetrics();

        this.projectPath=projectPath;
        ClassFinder.setProjectPath(projectPath);
        FileExplorer explorer = ClassFinder.getClassExplorer();

        for(String key : explorer.getCUMap().keySet())
        {
            for(int i=0; i<explorer.getCUMap().get(key).size(); i++)
            {
                CompilationUnit cu = explorer.getCUMap().get(key).get(i);
                ClassManager manager = new ClassManager(key, cu);

                manager.prepareFields();
                manager.generateCohesionGraph();

                classManagers.add(manager);
            }
        }
        generateCallGraph();
    }
    private void generateCallGraph()
    {
        for(ClassManager classManager: classManagers)
        {
            for(MethodManager methodManager: classManager.getMethodManagers())
            {
                callGraphMetrics.addCouplingHandler(methodManager.getCouplingHandler());
            }
        }
        callGraphMetrics.generateCouplingGraph();
        callGraphMetrics.generateMethodCallGraph();
    }
    public void printCoupling()
    {
        Set<String> coupleClasses = callGraphMetrics.getCoupleClasses();
        for(String couple : coupleClasses)
        {
            System.out.println(couple);
        }
    }
}