package SystemMonitor;

import java.io.*;

public class LogAnalyzer {
    public static void main(String[] args) {
        String fileName = "system_stats.csv";

        double maxCpu = 0, minCpu = 100, sumCpu = 0;
     
        long maxRam = 0, minRam = Long.MAX_VALUE, sumRam = 0;
        long maxHeap = 0, minHeap = Long.MAX_VALUE, sumHeap = 0;
        
        int count = 0;

        System.out.println("🔍 Initializing Log Analysis Engine...");

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
               
                String[] data = line.split(", ");
                
                
                if (data.length < 4) continue; 

                try {
                    double cpu = Double.parseDouble(data[1]);
                    long ram = Long.parseLong(data[2]);
                    long heap = Long.parseLong(data[3].trim());

                    if (cpu > maxCpu) maxCpu = cpu;
                    if (cpu < minCpu) minCpu = cpu;
                    sumCpu += cpu;

                    if (ram > maxRam) maxRam = ram;
                    if (ram < minRam) minRam = ram;
                    sumRam += ram;

                    if (heap > maxHeap) maxHeap = heap;
                    if (heap < minHeap) minHeap = heap;
                    sumHeap += heap;

                    count++;
                } catch (NumberFormatException e) {
                    continue; 
                }
            }

            if (count > 0) {
                displayReport(count, minCpu, maxCpu, sumCpu/count, 
                              minRam, maxRam, sumRam/count, 
                              minHeap, maxHeap, sumHeap/count);
            } else {
                System.out.println("⚠️ No valid log entries found. Ensure 'system_stats.csv' exists and has data.");
            }

        } catch (IOException e) {
            System.err.println("Critical Error: Could not access log file. " + e.getMessage());
        }
    }

    private static void displayReport(int total, double minC, double maxC, double avgC, 
                                     long minR, long maxR, long avgR, 
                                     long minH, long maxH, long avgH) {
        
        System.out.println("\n==================================================");
        System.out.println("          SYSTEM PERFORMANCE SUMMARY REPORT       ");
        System.out.println("==================================================");
        System.out.println("Total Samples Analyzed : " + total);
        System.out.println("Monitoring Duration    : ~" + (total * 2) + " seconds");
        System.out.println("--------------------------------------------------");
        
        System.out.printf("METRIC       |   MINIMUM   |   MAXIMUM   |   AVERAGE   %n");
        System.out.println("-------------|-------------|-------------|-------------");
        System.out.printf("CPU LOAD     | %10.2f%% | %10.2f%% | %10.2f%%%n", minC, maxC, avgC);
        System.out.printf("SYSTEM RAM   | %7d MB | %7d MB | %7d MB%n", minR, maxR, avgR);
        System.out.printf("JAVA HEAP    | %7d MB | %7d MB | %7d MB%n", minH, maxH, avgH);
        
        System.out.println("==================================================");
     
        if (maxC > 85.0) {
            System.out.println("🚨 STATUS: Critical CPU spikes detected.");
        } else if (maxR > (minR * 1.5)) {
            System.out.println("🚨 STATUS: Significant RAM fluctuation detected.");
        } else {
            System.out.println("✅ STATUS: System performance was stable.");
        }
        System.out.println("==================================================\n");
    }
}
