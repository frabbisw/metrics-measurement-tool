package support;

import collect_classes.FileExplorer;
import collect_classes.ClassFinder;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;

import java.io.File;
import java.util.*;

public class ClassManager {
    Map<String, String> classesMap;
    ArrayList<ClassOrInterfaceDeclaration>localClasses;
    ArrayList<MethodManager> methodManagers;
    String myPackageName="Not Declared";

    public ClassManager(String ParentPath, CompilationUnit compilationUnit) {
        FileExplorer fileExplorer = ClassFinder.getClassExplorer();
        classesMap = new TreeMap<>();
        localClasses=new ArrayList<>();
        methodManagers=new ArrayList<>();
        if(compilationUnit.getPackageDeclaration().isPresent())
            myPackageName=compilationUnit.getPackageDeclaration().get().getNameAsString();

        for (ImportDeclaration immport : compilationUnit.getImports())
        {
            String importName=immport.getNameAsString();
            if(fileExplorer.getClassNamesByImportTag(importName)!=null)
            {
                for(String className : fileExplorer.getClassNamesByImportTag(importName))
                {
                    if(immport.toString().contains("*"))    classesMap.put(className, importName+"."+className);
                    else classesMap.put(className, importName);
                }
            }
        }
        if(fileExplorer.getClassNamesBySource(ParentPath)!=null)
        {
            for(String className : fileExplorer.getClassNamesBySource(ParentPath))
                classesMap.put(className, myPackageName+"."+className);
        }

        for(TypeDeclaration type : compilationUnit.getTypes())
            localClasses.add(compilationUnit.getClassByName(type.getNameAsString()).get());
    }
    public void prepareFields()
    {
        for(ClassOrInterfaceDeclaration clazz : localClasses)
        {
            String classNameWithPackage=myPackageName+"."+clazz.getNameAsString();
            Map<String, String>globalMap = new TreeMap<>();

            for (FieldDeclaration field : clazz.getFields())
                for(VariableDeclarator variable : field.getVariables())
                    for(String flazz : classesMap.keySet())
                        if(flazz.equals(variable.getType().asString()))
                            globalMap.put(variable.getNameAsString(),variable.getType().asString());

            for(String string : classesMap.keySet())
                globalMap.put(string, string);

            convertAndAddConstructors(clazz, globalMap);

            for(MethodDeclaration method : clazz.getMethods())
                methodManagers.add(new MethodManager(method, classNameWithPackage, globalMap, classesMap));

            for(MethodManager manager : methodManagers)
                manager.printCoupling();
        }
    }

    private void convertAndAddConstructors(ClassOrInterfaceDeclaration clazz, Map<String, String>globalMap) {
        for(ConstructorDeclaration cd : clazz.getConstructors())
        {
            MethodDeclaration md = new MethodDeclaration();
            md.setName(cd.getName());
            md.setParameters(cd.getParameters());
            md.setBody(cd.getBody());

            methodManagers.add(new MethodManager(md, myPackageName+"."+cd.getNameAsString(), globalMap, classesMap));
        }
    }

    public static void main(String [] args) throws Exception
    {
        ClassFinder.setProjectPath("/home/rabbi/dl4j/soft_metrics");
        File myFile = new File("/home/rabbi/dl4j/soft_metrics/src/main/java/support/ClassManager.java");

        CompilationUnit unit = JavaParser.parse(myFile);
        ClassManager manager = new ClassManager(myFile.getParent(),unit);

        manager.prepareFields();
    }
}