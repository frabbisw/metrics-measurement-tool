package support;

import class_metrics.CohesionGraph;
import collect_classes.FileExplorer;
import collect_classes.ClassFinder;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;

import java.io.File;
import java.util.*;

public class CUManager {
    Map<String, String> classesMap;
    ArrayList<ClassManager>localClasses;
    ArrayList<MethodManager> methodManagers;
    Set<String>globalSet;
    String myPackageName="Not Declared";

    public CUManager(String ParentPath, CompilationUnit compilationUnit) {

        FileExplorer fileExplorer = ClassFinder.getClassExplorer();
        classesMap = new TreeMap<>();
        localClasses=new ArrayList<>();
        methodManagers=new ArrayList<>();
        globalSet=new HashSet<>();

        if(compilationUnit.getPackageDeclaration().isPresent())
            myPackageName=compilationUnit.getPackageDeclaration().get().getNameAsString();

        for (ImportDeclaration immport : compilationUnit.getImports())
        {
            String importName=immport.getNameAsString();
            if(fileExplorer.getClassNamesByImportTag(importName)!=null)
            {
                for(String className : fileExplorer.getClassNamesByImportTag(importName))
                {
                    if(immport.toString().contains("*"))    classesMap.put(className, getFullName(importName, className));
                    else classesMap.put(className, importName);
                }
            }
        }
        if(fileExplorer.getClassNamesBySource(ParentPath)!=null)
        {
            for(String className : fileExplorer.getClassNamesBySource(ParentPath))
                classesMap.put(className, getFullName(myPackageName, className));
        }

        for(TypeDeclaration type : compilationUnit.getTypes())
        {
            if(compilationUnit.getClassByName(type.getNameAsString()).isPresent())
                localClasses.add(new ClassManager(myPackageName, type.asClassOrInterfaceDeclaration(), classesMap));
            if(compilationUnit.getInterfaceByName(type.getNameAsString()).isPresent())
                localClasses.add(new ClassManager(myPackageName, type.asClassOrInterfaceDeclaration(), classesMap));
        }
    }
    private String getFullName(String packageName, String className)
    {
        return packageName+"."+className;
    }

    public ArrayList<ClassManager> getLocalClasses() {
        return localClasses;
    }
}