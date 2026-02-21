package SystemMonitor;

import java.io.*;

public class LogAnalyzer {
    public static void main(String[] args) {
        String fileName = "system_stats.csv";
        
        // CPU Stats (double)
        double maxCpu = 0, minCpu = 100, sumCpu = 0;
        
        // System RAM Stats (long - MB)
        long maxRam = 0, minRam = Long.MAX_VALUE, sumRam = 0;
        
        // Java Heap Stats (long - MB)
        long maxHeap = 0, minHeap = Long.MAX_VALUE, sumHeap = 0;
        
        int count = 0;

        System.out.println("🔍 Initializing Log Analysis Engine...");

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the 4 columns: [0]Timestamp, [1]CPU, [2]RAM, [3]Heap
                String[] data = line.split(", ");
                
                // Skip lines that don't match our new 4-column format
                if (data.length < 4) continue; 

                try {
                    double cpu = Double.parseDouble(data[1]);
                    long ram = Long.parseLong(data[2]);
                    long heap = Long.parseLong(data[3].trim());

                    // --- Process CPU ---
                    if (cpu > maxCpu) maxCpu = cpu;
                    if (cpu < minCpu) minCpu = cpu;
                    sumCpu += cpu;

                    // --- Process RAM ---
                    if (ram > maxRam) maxRam = ram;
                    if (ram < minRam) minRam = ram;
                    sumRam += ram;

                    // --- Process Heap ---
                    if (heap > maxHeap) maxHeap = heap;
                    if (heap < minHeap) minHeap = heap;
                    sumHeap += heap;

                    count++;
                } catch (NumberFormatException e) {
                    continue; // Skip any corrupted lines
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
        
        // Displaying results in a clean table format
        System.out.printf("METRIC       |   MINIMUM   |   MAXIMUM   |   AVERAGE   %n");
        System.out.println("-------------|-------------|-------------|-------------");
        System.out.printf("CPU LOAD     | %10.2f%% | %10.2f%% | %10.2f%%%n", minC, maxC, avgC);
        System.out.printf("SYSTEM RAM   | %7d MB | %7d MB | %7d MB%n", minR, maxR, avgR);
        System.out.printf("JAVA HEAP    | %7d MB | %7d MB | %7d MB%n", minH, maxH, avgH);
        
        System.out.println("==================================================");
        
        // Conditional Insight
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