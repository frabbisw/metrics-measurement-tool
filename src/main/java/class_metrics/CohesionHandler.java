package class_metrics;

import java.util.HashSet;
import java.util.Set;

public class CohesionHandler {
    String methodName;
    String className;
    Set<String>globalVariables;
    Set<String>calledMethods;

    public CohesionHandler(String methodName, String className)
    {
        this.methodName=methodName;
        this.className=className;

        globalVariables=new HashSet<>();
        calledMethods=new HashSet<>();
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }

    public Set<String> getGlobalVariables() {
        return globalVariables;
    }

    public Set<String> getCalledMethods() {
        return calledMethods;
    }

    public void addGlobalVariable(String v)
    {
        globalVariables.add(v);
    }
    public void addCalledMethod(String calledMethod)
    {
        calledMethods.add(calledMethod);
    }
    public void addToGraph(CohesionGraphAdapter adapter)
    {
        for(String v : globalVariables)
        {
            adapter.graphCallBack(methodName, v);
            adapter.graphCallBack(v, methodName);
        }

        for(String v : calledMethods)
        {
            adapter.graphCallBack(methodName, v);
            adapter.graphCallBack(v, methodName);
        }
    }
}