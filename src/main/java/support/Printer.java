package support;

import class_metrics.CohesionHandler;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Printer {
    public static void printCouplingFromMethod(String className, String methodName, Map<String, Set<String>> couplingMap)
    {
        System.out.print(Strings.repeat("=", methodName.length())+"\n");
        System.out.println(className+" "+methodName);
        System.out.println(Strings.repeat("=", methodName.length()));

        for(String classkey : couplingMap.keySet())
        {
            System.out.println("Dependent Class: "+classkey);
            System.out.print("Method calls from class '"+ classkey+"': ");
            for (String depMethod : couplingMap.get(classkey))
                System.out.print(depMethod+" ");
            System.out.println();
        }
    }
    public static void printCohesionFromMethod(CohesionHandler cohesionHandler)
    {
        System.out.print(Strings.repeat("=", cohesionHandler.getMethodName().length())+"\n");
        System.out.println(cohesionHandler.getClassName()+" "+cohesionHandler.getMethodName());
        System.out.println(Strings.repeat("=", cohesionHandler.getMethodName().length()));

        System.out.print("Dependent Methods inside class: ");
        for (String mn : cohesionHandler.getCalledMethods())
            System.out.print(mn+" ");
        System.out.println();
        System.out.print("Global variables: ");
        for (String mn : cohesionHandler.getGlobalVariables())
            System.out.print(mn+" ");
        System.out.println();
    }
}
