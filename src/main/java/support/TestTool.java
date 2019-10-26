package support;

public class TestTool {
    public static void main(String [] args) throws Exception
    {
        double time = System.nanoTime();

        MetricsTool tool = new MetricsTool("/home/rabbi/Desktop/re/sysmap/dataset/aes", "/home/rabbi/Desktop/re/sysmap/out/aes");
//        MetricsTool tool = new MetricsTool("/home/rabbi/Desktop/re/sysmap/dataset/JDeodorant-master", "/home/rabbi/Desktop/re/sysmap/out/JDeodorant");
//        MetricsTool tool = new MetricsTool("/home/rabbi/Desktop/re/sysmap/dataset/jaimlib", "/home/rabbi/Desktop/re/sysmap/out/jaimlib");
//        MetricsTool tool = new MetricsTool("/home/rabbi/IdeaProjects/metrics-measurement-tool", "/home/rabbi/Desktop/re/sysmap/out/our-mm");
//        MetricsTool tool = new MetricsTool("/home/rabbi/IdeaProjects/sysmap", "/home/rabbi/Desktop/re/sysmap/out/sysmap");

        tool.printCSV();
        //tool.test();

        System.out.println((System.nanoTime()-time)/1000000);
    }
}