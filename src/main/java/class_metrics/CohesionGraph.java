package class_metrics;

import com.google.common.hash.HashCode;

import java.util.*;

public class CohesionGraph {
    Map<String, ArrayList<String>>cohesionMap;
    Set<String>allMethods;
    Set<String> visited;

    int dsu=0;
    public CohesionGraph()
    {
        allMethods=new HashSet<>();
        cohesionMap=new TreeMap<>();
    }
    public void addToMap(CohesionHandler cohesionHandler)
    {
        allMethods.add(cohesionHandler.getMethodName());
        allMethods.addAll(cohesionHandler.getGlobalVariables());
        cohesionHandler.addToGraph(new CohesionGraphAdapter() {
            @Override
            public void graphCallBack(String key, String value) {
                if(!cohesionMap.containsKey(key))
                    cohesionMap.put(key, new ArrayList<>());
                cohesionMap.get(key).add(value);
            }
        });
    }
    public void showGraph() {
        for(String key : allMethods)
        {
            System.out.println(key+" :: "+cohesionMap.get(key));
        }
    }
    private void dfs(String node)
    {
        if(node==null)  return;
        visited.add(node);

        try {
            for(String child : cohesionMap.get(node))
            {
                if(!visited.contains(child))
                {
                    dfs(child);
                }
            }
        }
        catch (NullPointerException e){
            //System.out.println("Null Pointer Exception");
        }
    }
    public void calculateDSU()
    {
        dsu=0;
        visited=new HashSet<>();

        for (String node : allMethods)
        {
            if(!visited.contains(node))
            {
                dsu++;
                dfs(node);
            }
        }
    }
    public int getDsu()
    {
        return dsu;
    }
}