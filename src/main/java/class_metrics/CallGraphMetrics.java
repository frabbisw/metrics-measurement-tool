package class_metrics;

import java.util.*;

public class CallGraphMetrics {
    private ArrayList<CouplingHandler>couplingHandlers;
    Map<String, Set<String>> classCallMap;
    Map<String, Integer> couplingValues;
    Set<String> coupleClasses;
    Map<String, Integer>methodHash;
    Integer [][] callGraph;

    public CallGraphMetrics()
    {
        classCallMap =new TreeMap<>();
        couplingHandlers=new ArrayList<>();
        coupleClasses=new HashSet<>();
        methodHash=new HashMap<>();
        couplingValues=new TreeMap<>();
    }
    public void addCouplingHandler(CouplingHandler handler)
    {
        couplingHandlers.add(handler);
    }
    public void generateCouplingClass()
    {
        for(CouplingHandler handler : couplingHandlers)
        {
            if(!classCallMap.containsKey(handler.getClassName()))
            {
                classCallMap.put(handler.getClassName(), new HashSet<>());
            }
            classCallMap.get(handler.getClassName()).addAll(handler.getUsedClasses());

            for(String calledClass : handler.getUsedClasses())
            {
                if(!classCallMap.containsKey(calledClass))
                {
                    classCallMap.put(calledClass, new HashSet<>());
                }
                classCallMap.get(calledClass).add(handler.getFullName());
            }
        }
        for(String clazz : classCallMap.keySet())
        {
            couplingValues.put(clazz, classCallMap.get(clazz).size());
        }
    }
    public void generateMethodCallGraph()
    {
        int counter=0;
        for(CouplingHandler handler : couplingHandlers)
        {
            if(!methodHash.containsKey(handler.getFullName()))
                methodHash.put(handler.getFullName(), counter++);
            for(String methodName : handler.getCalledMethods())
            {
                if(!methodHash.containsKey(methodName))
                    methodHash.put(methodName, counter++);
            }
        }

        callGraph=new Integer[counter+1][counter+1];
        for(int i=0; i<callGraph.length; i++)
            for(int j=0; j<callGraph[i].length; j++)
                callGraph[i][j]=0;

        for(CouplingHandler source : couplingHandlers)
        {
            for(String target : source.getCalledMethods())
            {
                int a = methodHash.get(source.getFullName());
                int b = methodHash.get(target);
                callGraph[a][b]=1;
            }
        }
    }

    public Map<String, Integer> getCouplingValues() {
        return couplingValues;
    }

    public Map<String, Integer> getMethodHash() {
        return methodHash;
    }

    public Integer[][] getCallGraph() {
        return callGraph;
    }
}