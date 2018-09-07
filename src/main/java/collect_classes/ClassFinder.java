package collect_classes;

import java.io.File;

public class ClassFinder {
    static ClassExplorer explorer;
    static String projectPath;
    public static void setProjectPath(String path)
    {
        projectPath=path;
    }
    public static ClassExplorer getClassExplorer()
    {
        if(explorer==null)
            explorer=new ClassExplorer(new File(projectPath));
        return explorer;
    }
}
