package class_metrics;

import java.util.*;

public class CallGraphMetrics {
    private ArrayList<CouplingHandler>couplingHandlers;
    Map<String, Set<String>>classCallGraph;
    Set<String> coupleClasses;
    Map<String, Integer>methodHash;
    Integer [][] callGraph;

    public CallGraphMetrics()
    {
        classCallGraph=new TreeMap<>();
        couplingHandlers=new ArrayList<>();
        coupleClasses=new HashSet<>();
        methodHash=new HashMap<>();
    }
    public void addCouplingHandler(CouplingHandler handler)
    {
        couplingHandlers.add(handler);
    }
    public void generateCouplingGraph()
    {
        for(CouplingHandler handler : couplingHandlers)
        {
            if(!classCallGraph.containsKey(handler.getClassName()))
            {
                classCallGraph.put(handler.getClassName(), new HashSet<>());
            }
            classCallGraph.get(handler.getClassName()).addAll(handler.getUsedClasses());
        }

        for(String sourceClass : classCallGraph.keySet())
        {
            for(String targetClass : classCallGraph.get(sourceClass))
            {
                if(sourceClass.compareTo(targetClass)<0)
                    coupleClasses.add(sourceClass+"+"+targetClass);
                else
                    coupleClasses.add(targetClass+"+"+sourceClass);
            }
        }
    }
    public Set<String> getCoupleClasses()
    {
        return coupleClasses;
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
        System.out.println("\n");

        callGraph=new Integer[counter+1][counter+1];

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
    public int totalCoupling()
    {
        return coupleClasses.size();
    }
}
