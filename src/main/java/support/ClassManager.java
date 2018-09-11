package support;

import collect_classes.FileExplorer;
import collect_classes.ClassFinder;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ClassManager {
    ArrayList<String>foreignClasses;
    ArrayList<ClassOrInterfaceDeclaration>localClasses;
    ArrayList<MethodManager> methodManagers =new ArrayList<>();

    public ClassManager(String ParentPath, CompilationUnit compilationUnit) {
        FileExplorer fileExplorer = ClassFinder.getClassExplorer();
        foreignClasses = new ArrayList<>();
        localClasses=new ArrayList<>();
        methodManagers=new ArrayList<>();


        for (ImportDeclaration immport : compilationUnit.getImports())
        {
            if(fileExplorer.getClassNamesByImportTag(immport)!=null)
                foreignClasses.addAll(fileExplorer.getClassNamesByImportTag(immport));
        }
        if(fileExplorer.getClassNamesBySource(ParentPath)!=null)
            foreignClasses.addAll(fileExplorer.getClassNamesBySource(ParentPath));

        for(TypeDeclaration type : compilationUnit.getTypes())
            localClasses.add(compilationUnit.getClassByName(type.getNameAsString()).get());
    }
    public void prepareFields()
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

            convertAndAddConstructors(clazz, globalMap);

            for(MethodDeclaration method : clazz.getMethods())
                methodManagers.add(new MethodManager(method, globalMap, foreignClasses));

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

            methodManagers.add(new MethodManager(md, globalMap, foreignClasses));
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