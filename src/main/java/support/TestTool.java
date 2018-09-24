package support;

public class TestTool {
    public static void main(String [] args) throws Exception
    {
        double time = System.nanoTime();

        MetricsTool tool = new MetricsTool("/home/rabbi/Downloads/metric-dataset/proguard6.0.3", "/home/rabbi/Desktop/metricsOutput/");
        tool.printCSV();
        //tool.test();

        System.out.println((System.nanoTime()-time)/1000000);
    }
}