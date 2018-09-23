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

        calledMethods.add(generateFullName(className, methodName));
    }
    private String generateFullName(String className, String methodName)
    {
        return className+"::"+methodName;
    }
    public int getNumberOfCalledMethods()
    {
        return calledMethods.size();
    }

    public Set<String> getCalledMethods() {
        return calledMethods;
    }
    public String getClassName()
    {
        return myClassName;
    }
    public String getFullName()
    {
        return myClassName+"::"+myMethodName;
    }
    public Set<String> getUsedClasses()
    {
        return couplingMap.keySet();
    }
}