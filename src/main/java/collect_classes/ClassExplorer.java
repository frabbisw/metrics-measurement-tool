package collect_classes;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ClassExplorer
{
    Map <String, ArrayList<String>> packageMap;
    Map <String, ArrayList<String>> packMapBySrc;

    public ClassExplorer(File rootFile)
    {
        packageMap = new TreeMap<>();
        packMapBySrc = new TreeMap<>();

        browseClasses(rootFile);
    }
    public Map<String, ArrayList<String>> getPackageMap()
    {
        return packageMap;
    }
    public Map<String, ArrayList<String>> getPackMapBySrc() {
        return packMapBySrc;
    }

    public void browseClasses(File rootFile) {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            String src = file.getParent();
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(CompilationUnit n, Object arg) {
                        super.visit(n, arg);

                        if(n.getPackageDeclaration().isPresent())
                        {
                            String pack = n.getPackageDeclaration().get().getNameAsString();
                            if(!packageMap.containsKey(pack))
                                packageMap.put(pack, new ArrayList<>());

                            for(TypeDeclaration type : n.getTypes())
                            {
                                packageMap.get(pack).add(type.getNameAsString());

                                try {
                                    String className=n.getClassByName(type.getNameAsString()).get().getNameAsString();
                                    packageMap.put(pack+"."+className, new ArrayList<>());
                                    packageMap.get(pack+"."+className).add(type.getNameAsString());
                                }
                                catch (Exception e) {}
                            }
                        }
                        if(!packMapBySrc.containsKey(src))
                            packMapBySrc.put(src, new ArrayList<>());
                        for(TypeDeclaration type : n.getTypes()) {
                            packMapBySrc.get(src).add(type.getNameAsString());
                        }
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }).explore(rootFile);
    }
    public ArrayList<String>getClassesByImportTag(ImportDeclaration declaration)
    {
        if(packageMap.containsKey(declaration.getNameAsString()))
            return packageMap.get(declaration.getNameAsString());
        return null;
    }
    public ArrayList<String>getClassesBySource(String path)
    {
        if(packMapBySrc.containsKey(path))
            return packMapBySrc.get(path);
        return null;
    }
    /*
    public static void main(String[] args) {
        File rootFile = new File("/home/rabbi/bin/samples/effective-java-examples-master");
        ClassExplorer explorer = new ClassExplorer(rootFile);

        Map <String, ArrayList<String>> packageMap = explorer.getPackageMap();
        for(String key : packageMap.keySet())
        {
            System.out.println("package: "+key);
            System.out.println("Classes: ");
            for (String str : packageMap.get(key))
            {
                System.out.println(str);
            }
        }
    }
    */
}