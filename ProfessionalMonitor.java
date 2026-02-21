package SystemMonitor;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class ProfessionalMonitor {
    private static volatile boolean running = true;

    public static void main(String[] args) {
        // --- SHUTDOWN HOOK ---
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            System.out.println("\n[System] Shutdown signal received. Closing logs safely...");
        }));

        Thread monitorThread = new Thread(() -> {
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            int highCpuCount = 0;

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("system_stats.csv", true))) {
                while (running) {
                    // 1. COLLECT CPU DATA
                    double cpu = osBean.getSystemCpuLoad() * 100;
                    if (cpu < 0) { 
                        Thread.sleep(1000); 
                        continue; 
                    }

                    // 2. COLLECT MEMORY DATA (System RAM & Java Heap)
                    long totalRAM = osBean.getTotalPhysicalMemorySize() / (1024 * 1024);
                    long freeRAM = osBean.getFreePhysicalMemorySize() / (1024 * 1024);
                    long usedRAM = totalRAM - freeRAM;
                    
                    long heapUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    long heapMB = heapUsed / (1024 * 1024);

                    // 3. WRITE TO CSV (Format: Timestamp, CPU%, UsedRAM_MB, HeapMB)
                    String log = String.format("%s, %.2f, %d, %d\n", 
                                 LocalDateTime.now(), cpu, usedRAM, heapMB);
                    bw.write(log);
                    bw.flush(); 

                    // 4. REAL-TIME ALERT LOGIC
                    if (cpu > 80.0) {
                        highCpuCount++;
                        if (highCpuCount >= 3) {
                            System.err.println("\n⚠️ ALERT: Sustained High CPU (" + String.format("%.2f", cpu) + "%)");
                        }
                    } else {
                        highCpuCount = 0; 
                    }
                    
                    Thread.sleep(2000); 
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Monitor Error: " + e.getMessage());
            }
        });

        monitorThread.start();
        
        System.out.println("🚀 Monitor Active (CPU & RAM). Commands: [stop] [status]");
        Scanner scanner = new Scanner(System.in);

        while (running) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("stop")) {
                    running = false;
                    break;
                } else if (input.equalsIgnoreCase("status")) {
                    System.out.println("Service Status: HEALTHY | Threads: " + Thread.activeCount());
                }
            }
        }

        try {
            monitorThread.join(5000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("✅ Project Complete.");
    }
}