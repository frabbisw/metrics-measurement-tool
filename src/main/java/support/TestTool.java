package support;

public class TestTool {
    public static void main(String [] args) throws Exception
    {
        double time = System.nanoTime();

        MetricsTool tool = new MetricsTool("/home/rabbi/dl4j/soft_metrics", "/home/rabbi/Desktop/metricsOutput.csv");
        tool.printCSV();

        System.out.println((System.nanoTime()-time)/1000000);
    }
}