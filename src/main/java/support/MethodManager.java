package support;

import class_metrics.CohesionHandler;
import class_metrics.CouplingHandler;
import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

public class MethodManager {
    Map<String, String> localMap;
    //Map<String, Set<String>>couplingMap;
    CohesionHandler cohesionHandler;
    CouplingHandler couplingHandler;
    Map<String,String>classesMap;
    Map<String, String> globalMap;
    String myClassName;
    String methodName="";

    public MethodManager(MethodDeclaration method, String myClassName, Map<String, String> globalMap, Map<String,String>classesMap, Set<String>globalSet)
    {
        this.globalMap=globalMap;
        this.classesMap=classesMap;
        this.methodName=method.getNameAsString();
        this.myClassName=myClassName;

        localMap = new TreeMap<>();
        couplingHandler=new CouplingHandler(methodName, myClassName);
        cohesionHandler=new CohesionHandler(methodName, myClassName);

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
                if(globalSet.contains(forEach.getIterable().toString()))
                    cohesionHandler.addVariable(forEach.getIterable().toString());
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
                if(globalSet.contains(expr.getTarget().toString()))
                    cohesionHandler.addVariable(expr.getTarget().toString());
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

            couplingHandler.addCalledMethod(fullClassName, methodName);
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

                couplingHandler.addCalledMethod(fullClassName, methodName);
            }
        }
        else
            cohesionHandler.addCalledMethod(methodCall.getNameAsString());
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

    public CohesionHandler getCohesionHandler() {
        return cohesionHandler;
    }

    public CouplingHandler getCouplingHandler() {
        return couplingHandler;
    }

    public void printCohesion()
    {
        Printer.printCohesionFromMethod(cohesionHandler);
    }
    public void printCoupling()
    {
        //Printer.printCouplingFromMethod(methodName, myClassName, couplingMap);
    }
}