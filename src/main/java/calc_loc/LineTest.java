package calc_loc;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.File;

public class LineTest {
    public static void main(String [] args) throws Exception
    {
        CompilationUnit cu = JavaParser.parse(new File("/home/rabbi/dl4j/soft_metrics/src/main/java/support/ClassManager.java"));

        for(TypeDeclaration type : cu.getTypes())
        {
            ClassOrInterfaceDeclaration clazz = cu.getClassByName(type.getNameAsString()).get();

            LineCounter counter = new LineCounter(clazz);
            System.out.println(counter.getLineOfComments());
        }
    }
}
