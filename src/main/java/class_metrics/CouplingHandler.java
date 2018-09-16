package class_metrics;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CouplingHandler {
    String myMethodName;
    String myClassName;
    Map<String, Set<String>> couplingMap;
    Set<String>calledMethods;

    public CouplingHandler(String myMethodName, String myClassName)
    {
        this.myMethodName=myMethodName;
        this.myClassName=myClassName;

        calledMethods=new HashSet<>();
        couplingMap=new TreeMap<>();
    }
    public void addCalledMethod(String className, String methodName)
    {
        if(!couplingMap.containsKey(className))
            couplingMap.put(className, new HashSet<>());
        couplingMap.get(className).add(methodName);

        calledMethods.add(className+"::"+methodName);
    }
    public int getNumberOfCalledMethods()
    {
        return calledMethods.size();
    }

    public Set<String> getCalledMethods() {
        return calledMethods;
    }
}
