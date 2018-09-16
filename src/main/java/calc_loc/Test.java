package calc_loc;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;

public class Test {
    public static void main(String [] args) throws Exception
    {
        CompilationUnit cu = JavaParser.parse(new File("/home/rabbi/bin/samples/hudai/A.java"));
        LineCounter counter = new LineCounter(cu);

        System.out.println(counter.getNumberOfStatements());
    }
}
