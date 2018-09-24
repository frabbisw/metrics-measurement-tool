package collect_classes;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class FileExplorer
{
    Map <String, ArrayList<String>> packageMap;
    Map <String, ArrayList<String>> sourceMap;
    Map<String, ArrayList<CompilationUnit>> CUMap;

    public FileExplorer(File rootFile)
    {
        packageMap = new TreeMap<>();
        sourceMap = new TreeMap<>();
        CUMap = new TreeMap<>();

        browseClasses(rootFile);
    }
    public Map<String, ArrayList<String>> getPackageMap()
    {
        return packageMap;
    }
    public Map<String, ArrayList<String>> getSourceMap() {
        return sourceMap;
    }

    public void browseClasses(File rootFile) {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            String src = file.getParent();
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(CompilationUnit n, Object arg) {
                        super.visit(n, arg);

                        String packName=null;
                        if(n.getPackageDeclaration().isPresent())
                        {
                            packName=n.getPackageDeclaration().get().getNameAsString();
                            if(!CUMap.containsKey(src))
                                CUMap.put(src, new ArrayList<>());
                            CUMap.get(src).add(n);
                            includeToPackageMap(n);
                        }
                        if(!sourceMap.containsKey(src))
                            sourceMap.put(src, new ArrayList<>());
                        for(TypeDeclaration type : n.getTypes()) {
                            sourceMap.get(src).add(type.getNameAsString());
                        }
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }).explore(rootFile);
    }
    private void includeToPackageMap(CompilationUnit n) {
        String packName = n.getPackageDeclaration().get().getNameAsString();
        if(!packageMap.containsKey(packName))
            packageMap.put(packName, new ArrayList<>());

        for(TypeDeclaration type : n.getTypes())
        {
            packageMap.get(packName).add(type.getNameAsString());

            try {
                String className=n.getClassByName(type.getNameAsString()).get().getNameAsString();
                packageMap.put(packName+"."+className, new ArrayList<>());
                packageMap.get(packName+"."+className).add(type.getNameAsString());
            }
            catch (Exception e) {}
        }
    }
    private String getFullName(String packName, String className)
    {
        if(packName==null)  return className;
        return packName+className;
    }
    public ArrayList<String> getClassNamesByImportTag(String packageName)
    {
        if(packageMap.containsKey(packageName))
            return packageMap.get(packageName);
        return null;
    }
    public ArrayList<String> getClassNamesBySource(String path)
    {
        if(sourceMap.containsKey(path))
            return sourceMap.get(path);
        return null;
    }
    public Map<String, ArrayList<CompilationUnit>>getCUMap()
    {
        return CUMap;
    }
}