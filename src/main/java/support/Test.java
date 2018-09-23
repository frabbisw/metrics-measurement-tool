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

        MetricsTool tool = new MetricsTool("/home/rabbi/dl4j/soft_metrics");
        tool.printCoupling();

        System.out.println((System.nanoTime()-time)/1000000);
    }
}