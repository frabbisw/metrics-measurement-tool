package support;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Map;

public class Printer {
    public static void printCouplingFromMethod(String methodName, Map<String, ArrayList<String>> couplingMap)
    {
        System.out.print(Strings.repeat("=", methodName.length())+"\n");
        System.out.println(methodName);
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
    public static void printCohessionFromMethod(String methodName, ArrayList<String>cohessinList)
    {
        System.out.print(Strings.repeat("=", methodName.length())+"\n");
        System.out.println(methodName);
        System.out.println(Strings.repeat("=", methodName.length()));

        System.out.print("Dependent Methods inside class: ");
        for (String mn : cohessinList)
            System.out.print(mn+" ");
        System.out.println();
    }
}
