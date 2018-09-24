package method_metrics;

import java.util.List;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

public class CyclomaticComplexityCalculator {

    private MethodDeclaration method;

    public CyclomaticComplexityCalculator(MethodDeclaration method) {
        this.method=method;
    }

    public int calculateComplexity() {
        int ifStatement=method.findAll(IfStmt.class).size();
        int forStatement=method.findAll(ForStmt.class).size();
        int forEachStatement=method.findAll(ForeachStmt.class).size();
        int whileStatement=method.findAll(WhileStmt.class).size();
        int doWhileStatement=method.findAll(DoStmt.class).size();
        int tryCatchStatement=method.findAll(TryStmt.class).size();
        int switchStatement=countBreak(method);
        int symbols=countSymbols(method.toString());

        int complexity=ifStatement+forStatement+forEachStatement+whileStatement+doWhileStatement+switchStatement+tryCatchStatement+symbols+1;

        return complexity;


    }
    private int countBreak(MethodDeclaration method) {
        List<SwitchEntryStmt> switchStmtList=method.findAll(SwitchEntryStmt.class);
        int totalBreak=0;
        for (SwitchEntryStmt switchEntryStmt : switchStmtList) {
            String str=switchEntryStmt.toString();
            totalBreak+= (str.split("case(?s:.*)break")).length-1;
        }
        return totalBreak;
    }

    private int countSymbols(String str) {
        return str.split("&&|[?]|\\|\\|").length-1;
    }

}