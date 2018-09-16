package support;

import collect_classes.ClassFinder;
import collect_classes.FileExplorer;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;

public class Test {
    public static void main(String [] args) throws Exception
    {
        double time = System.nanoTime();
        //ClassFinder.setProjectPath("/home/rabbi/dl4j/soft_metrics");
        //File myFile = new File("/home/rabbi/dl4j/soft_metrics/src/main/java/support/ClassManager.java");

        ClassFinder.setProjectPath("/home/rabbi/dl4j/soft_metrics");

        System.out.println((System.nanoTime()-time)/1000000);

        //File myFile = new File("/home/rabbi/dl4j/soft_metrics/src/main/java/support/ClassManager.java");


        FileExplorer explorer = ClassFinder.getClassExplorer();

        for(String key : explorer.getCUMap().keySet())
        {
            for(int i=0; i<explorer.getCUMap().get(key).size(); i++)
            {
                CompilationUnit cu = explorer.getCUMap().get(key).get(i);
                ClassManager manager = new ClassManager(key, cu);

                System.out.println(manager.getMethodManagers().size());
            }
        }

        System.out.println((System.nanoTime()-time)/1000000);

        //manager.prepareFields();
        //manager.generateCohesionGraph();

        //System.out.println((System.nanoTime()-time)/1000000);

        //System.out.println(manager.responseOfClass());
    }
}
