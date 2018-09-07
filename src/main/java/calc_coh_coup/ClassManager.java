package calc_coh_coup;

import collect_classes.ClassExplorer;
import collect_classes.ClassFinder;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ClassManager {
    ArrayList<String>foreignClasses;
    ArrayList<ClassOrInterfaceDeclaration>localClasses;

    public ClassManager(String ParentPath, CompilationUnit compilationUnit) {
        ClassExplorer classExplorer = ClassFinder.getClassExplorer();
        foreignClasses = new ArrayList<>();
        localClasses=new ArrayList<>();

        for (ImportDeclaration immport : compilationUnit.getImports())
        {
            if(classExplorer.getClassesByImportTag(immport)!=null)
                foreignClasses.addAll(classExplorer.getClassesByImportTag(immport));
        }
        if(classExplorer.getClassesBySource(ParentPath)!=null)
            foreignClasses.addAll(classExplorer.getClassesBySource(ParentPath));

        for(TypeDeclaration type : compilationUnit.getTypes())
            localClasses.add(compilationUnit.getClassByName(type.getNameAsString()).get());
    }
    public void calcFields()
    {
        for(ClassOrInterfaceDeclaration clazz : localClasses)
        {
            Map<String, String>globalMap = new TreeMap<>();

            for (FieldDeclaration field : clazz.getFields())
                for(VariableDeclarator variable : field.getVariables())
                    for(String flazz : foreignClasses)
                        if(flazz.equals(variable.getType().asString()))
                            globalMap.put(variable.getNameAsString(),variable.getType().asString());

            for(String string : foreignClasses)
                globalMap.put(string, string);

            for(MethodDeclaration method : clazz.getMethods())
                prepareMethod(method, globalMap);
        }
    }
    private void prepareMethod(MethodDeclaration method, Map<String, String> globalMap) {
        Map<String, String> localMap = new TreeMap<>();
        Map<String, ArrayList<String>>couplingMap = new TreeMap<>();
        ArrayList<String>cohessionList = new ArrayList<>();

        for (Parameter parameter : method.getParameters())
            for(String flazz : foreignClasses)
                if(flazz.equals(parameter.getTypeAsString()))
                    localMap.put(parameter.getNameAsString(), parameter.getTypeAsString());

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(VariableDeclarator variable, Void arg) {
                for(String flazz : foreignClasses)
                    if(flazz.equals(variable.getType().asString()))
                        localMap.put(variable.getNameAsString(), variable.getTypeAsString());
                super.visit(variable, arg);
            }

            @Override
            public void visit(MethodCallExpr methodCall, Void arg) {
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

                super.visit(methodCall, arg);
            }
        }, null);

        //Printer.printCouplingFromMethod(method.getNameAsString(), couplingMap);
        //Printer.printCohessionFromMethod(method.getNameAsString(), cohessionList);
        System.out.println();
    }

    /*
    public static void main(String [] args) throws Exception
    {
        ClassFinder.setProjectPath("/home/rabbi/dl4j/soft_metrics");
        File myFile = new File("/home/rabbi/dl4j/soft_metrics/src/main/java/calc_coh_coup/ClassManager.java");

        CompilationUnit unit = JavaParser.parse(myFile);
        ClassManager manager = new ClassManager(myFile.getParent(),unit);

        manager.calcFields();
    }
    */
}