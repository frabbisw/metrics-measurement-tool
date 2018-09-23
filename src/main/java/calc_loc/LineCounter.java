package calc_loc;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LineCounter {
    int lineOfComments;
    int numberOfComments;
    int lineOfCodes;
    int numberOfStatements;

    public LineCounter(ClassOrInterfaceDeclaration clazz)
    {
        numberOfStatements=0;
        lineOfComments=0;
        numberOfComments=0;

        lineOfCodes = clazz.getRange().get().end.line-clazz.getRange().get().begin.line+1;
        Map<Integer, Integer>commentMap=new TreeMap<>();

        List<Comment> comments=clazz.getAllContainedComments();
        for(Comment comment : comments)
            commentMap.put(comment.getRange().get().begin.line, comment.getRange().get().end.line);

        for(Integer i : commentMap.keySet())
        {
            numberOfComments++;
            lineOfComments+=commentMap.get(i)-i+1;
        }

        countStatements(clazz);
    }

    private void countStatements(ClassOrInterfaceDeclaration clazz) {
        clazz.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(AssignExpr n, Void arg) {
                numberOfStatements++;
                super.visit(n, arg);
            }

            @Override
            public void visit(ContinueStmt n, Void arg) {
                numberOfStatements++;
                super.visit(n, arg);
            }

            @Override
            public void visit(BreakStmt n, Void arg) {
                numberOfStatements++;
                super.visit(n, arg);
            }

            @Override
            public void visit(DoStmt n, Void arg) {
                numberOfStatements++;
                super.visit(n, arg);
            }

            @Override
            public void visit(ReturnStmt n, Void arg) {
                numberOfStatements++;
                super.visit(n, arg);
            }

            @Override
            public void visit(ObjectCreationExpr n, Void arg) {
                numberOfStatements++;
                super.visit(n, arg);
            }

            @Override
            public void visit(MethodCallExpr n, Void arg) {
                numberOfStatements++;
                super.visit(n, arg);
            }

            @Override
            public void visit(IfStmt n, Void arg) {
                numberOfStatements++;
                super.visit(n, arg);
            }

            @Override
            public void visit(ConditionalExpr n, Void arg) {
                numberOfStatements++;
                super.visit(n, arg);
            }
        }, null);
    }

    public int getLineOfComments() {
        return lineOfComments;
    }
    public int getNumberOfComments() {
        return numberOfComments;
    }
    public int getLineOfCodes()
    {
        return lineOfCodes;
    }
    public int getNumberOfStatements()
    {
        return numberOfStatements;
    }
}