package support;

import java.util.*;

public class InheritenceHandler {
    ArrayList<ClassManager>classManagers;
    Map<String, HashSet<String>> childrenMap;
    Map<String, ArrayList<String>> parentsMap;
    Map<String, Integer> levelMap;
    public InheritenceHandler(ArrayList<ClassManager>classManagers)
    {
        this.classManagers=classManagers;

        prepareInheritenceMap();
        prepareLevelOfInheritences();
    }

    private void prepareLevelOfInheritences() {
        levelMap=new TreeMap<>();

        for(ClassManager classManager : classManagers)
        {
            levelMap.put(classManager.getMyFullName(), calculateLevel(classManager.getMyFullName()));
        }
    }

    private Integer calculateLevel(String className) {
        if(!parentsMap.containsKey(className))  return 0;

        int max=0;
        for(String parent : parentsMap.get(className))
        {
            if(!levelMap.containsKey(parent))
                max=Math.max(max, 1+calculateLevel(parent));
            else
                max=Math.max(max, 1+levelMap.get(parent));
        }

        levelMap.put(className, max);
        return max;
    }

    private void prepareInheritenceMap() {
        this.childrenMap = new TreeMap<>();
        this.parentsMap = new TreeMap<>();

        for (ClassManager classManager : classManagers)
        {
            parentsMap.put(classManager.getMyFullName(), classManager.getParents());
            for(String parent : classManager.getParents())
            {
                if(parent==null){

                }
                else {
                    if(!childrenMap.containsKey(parent))
                        childrenMap.put(parent,new HashSet<>());
                    childrenMap.get(parent).add(classManager.getMyFullName());
                }
            }
        }
    }

    public double getNumberOfChildren(String className)
    {
        if(!childrenMap.containsKey(className))  return 0;
        return childrenMap.get(className).size();
    }
    public double getLevelOfInheritence(String className)
    {
        if(!levelMap.containsKey(className))    return 0;
        return levelMap.get(className);
    }
}
