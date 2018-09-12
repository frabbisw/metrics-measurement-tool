package collect_classes;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class ClassFinder {
    static FileExplorer explorer;
    static String projectPath;
    private Map<String,Integer> methodSerial;
    private Map<Integer,String> serialMethod;

    public static void setProjectPath(String path)
    {
        projectPath=path;
        explorer=new FileExplorer(new File(projectPath));

    }
    public static FileExplorer getClassExplorer()
    {
        return explorer;
    }
    public String getMethodNameBySerial(int serial)
    {
        return serialMethod.get(serial);
    }
    public int getSerialByMethodName(String methodName)
    {
        return methodSerial.get(methodName);
    }
    public void setMethodNameBySerial(int serial, String methodName)
    {
        serialMethod.put(serial,methodName);
    }
    public void setSerialByMethodName(String methodName, int serial)
    {
        methodSerial.put(methodName, serial);
    }
}