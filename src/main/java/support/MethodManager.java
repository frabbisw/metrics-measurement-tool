package support;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class MethodManager {
    Map<String, String> localMap;
    Map<String, ArrayList<String>>couplingMap;
    ArrayList<String>cohessionList;
    ArrayList<String>foreignClasses;
    Map<String, String> globalMap;

    String methodName="";

    public MethodManager(MethodDeclaration method, Map<String, String> globalMap, ArrayList<String>foreignClasses)
    {
        localMap = new TreeMap<>();
        couplingMap = new TreeMap<>();
        cohessionList = new ArrayList<>();

        this.globalMap=globalMap;
        this.foreignClasses=foreignClasses;
        this.methodName=method.getNameAsString();

        for (Parameter parameter : method.getParameters())
            for(String flazz : foreignClasses)
                if(flazz.equals(parameter.getTypeAsString()))
                    localMap.put(parameter.getNameAsString(), parameter.getTypeAsString());

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(VariableDeclarator variable, Void arg) {
                addVariableToLocalMap(variable, arg);
                super.visit(variable, arg);
            }
            @Override
            public void visit(ForeachStmt forEach , Void arg)
            {
                forEach.accept(new VoidVisitorAdapter<Void>() {
                    @Override
                    public void visit(VariableDeclarator variable, Void arg) {
                        addVariableToLocalMap(variable, arg);
                        super.visit(variable, arg);
                    }
                }, null);
                super.visit(forEach, arg);
            }

            @Override
            public void visit(MethodCallExpr methodCall, Void arg) {
                inspectMethodCall(methodCall, arg);
                super.visit(methodCall, arg);
            }
        }, null);
    }
    private void inspectMethodCall(MethodCallExpr methodCall, Void arg)
    {
        if(methodCall.getScope().isPresent())
        {
            String objectName=methodCall.getScope().get().toString();
            String className=null;
            if(globalMap.containsKey(objectName))   className=globalMap.get(objectName);
            if(localMap.containsKey(objectName))   className=localMap.get(objectName);

            if(className!=null)
            {
                String methodName=methodCall.getNameAsString();
                if(!couplingMap.containsKey(className))
                    couplingMap.put(className, new ArrayList<>());
                couplingMap.get(className).add(methodName);
            }
        }
        else
            cohessionList.add(methodCall.getNameAsString());
    }
    private void addVariableToLocalMap(VariableDeclarator variable, Void arg)
    {
        boolean isAForeignClass=false;
        for(String flazz : foreignClasses)
            if(flazz.equals(variable.getType().asString()))
            {
                localMap.put(variable.getNameAsString(), variable.getTypeAsString());
                isAForeignClass=true;
            }
        if(!isAForeignClass)
            localMap.put(variable.getNameAsString(), null);
    }
    public void printCohesion()
    {
        Printer.printCohessionFromMethod(methodName, cohessionList);
    }
    public void printCoupling()
    {
        Printer.printCouplingFromMethod(methodName, couplingMap);
    }
}