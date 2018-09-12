package support;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

public class MethodManager {
    Map<String, String> localMap;
    Map<String, Set<String>>couplingMap;
    Set<String>cohessionList;
    Map<String,String>classesMap;
    Map<String, String> globalMap;
    String myClassName;
    String methodName="";

    public MethodManager(MethodDeclaration method, String myClassName, Map<String, String> globalMap, Map<String,String>classesMap)
    {
        localMap = new TreeMap<>();
        couplingMap = new TreeMap<>();
        cohessionList = new HashSet<>();

        this.globalMap=globalMap;
        this.classesMap=classesMap;
        this.methodName=method.getNameAsString();
        this.myClassName=myClassName;

        for (Parameter parameter : method.getParameters())
            for(String flazz : classesMap.keySet())
                if(flazz.equals(parameter.getTypeAsString()))
                    localMap.put(parameter.getNameAsString(), parameter.getTypeAsString());

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(VariableDeclarator variable, Void arg) {
                addVariableToLocalMap(variable);
                super.visit(variable, arg);
            }
            @Override
            public void visit(ForeachStmt forEach , Void arg)
            {
                forEach.accept(new VoidVisitorAdapter<Void>() {
                    @Override
                    public void visit(VariableDeclarator variable, Void arg) {
                        addVariableToLocalMap(variable);
                        super.visit(variable, arg);
                    }
                }, null);
                super.visit(forEach, arg);
            }

            @Override
            public void visit(MethodCallExpr methodCall, Void arg) {
                inspectMethodCall(methodCall);
                super.visit(methodCall, arg);
            }

            @Override
            public void visit(AssignExpr expr, Void arg) {
                if(expr.isObjectCreationExpr())
                    addConstructorAsMethodCall(expr.asObjectCreationExpr());
                super.visit(expr, arg);
            }
        }, null);
    }

    private void addConstructorAsMethodCall(ObjectCreationExpr expr) {
        String className=expr.getType().asString();
        if(classesMap.containsKey(className))
        {
            String fullClassName=classesMap.get(className);
            String methodName=className;

            if(!couplingMap.containsKey(fullClassName))
                couplingMap.put(fullClassName, new HashSet<>());
            couplingMap.get(fullClassName).add(methodName);
        }
    }

    private void inspectMethodCall(MethodCallExpr methodCall)
    {
        for (Expression expression : methodCall.getArguments())
        {
            if (expression.isObjectCreationExpr())
                addConstructorAsMethodCall(expression.asObjectCreationExpr());
            if(expression.isMethodCallExpr())
                inspectMethodCall(expression.asMethodCallExpr());
        }
        if(methodCall.getScope().isPresent())
        {
            String objectName=methodCall.getScope().get().toString();
            String className=null;
            if(globalMap.containsKey(objectName))   className=globalMap.get(objectName);
            if(localMap.containsKey(objectName))   className=localMap.get(objectName);

            if(className!=null)
            {
                String fullClassName=classesMap.get(className);
                String methodName=methodCall.getNameAsString();
                if(!couplingMap.containsKey(fullClassName))
                    couplingMap.put(fullClassName, new HashSet<>());
                couplingMap.get(fullClassName).add(methodName);
            }
        }
        else
            cohessionList.add(methodCall.getNameAsString());
    }
    private void addVariableToLocalMap(VariableDeclarator variable)
    {
        boolean isAForeignClass=false;
        for(String flazz : classesMap.keySet())
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
        //Printer.printCohesionFromMethod(methodName, myClassName, cohessionList);
    }
    public void printCoupling()
    {
        Printer.printCouplingFromMethod(methodName, myClassName, couplingMap);
    }
}