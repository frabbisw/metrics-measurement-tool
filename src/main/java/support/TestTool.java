package support;

public class TestTool {
    public static void main(String [] args) throws Exception
    {
        double time = System.nanoTime();

        MetricsTool tool = new MetricsTool("/home/rabbi/dl4j/soft_metrics");
        tool.printAll();

        System.out.println((System.nanoTime()-time)/1000000);
    }
}