package class_metrics;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CouplingHandler {
    String myMethodName;
    String myClassName;
    Map<String, Set<String>> couplingMap;

    public CouplingHandler(String myMethodName, String myClassName)
    {
        this.myMethodName=myMethodName;
        this.myClassName=myClassName;

        couplingMap=new TreeMap<>();
    }
    public void addCalledMethod(String className, String methodName)
    {
        if(!couplingMap.containsKey(className))
            couplingMap.put(className, new HashSet<>());
        couplingMap.get(className).add(methodName);
    }
    public int getNumberOfCalledMethods()
    {
        return couplingMap.size();
    }
}
