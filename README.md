# Java System Resource Monitor

A Java-based application to monitor system CPU and memory usage and analyze the collected data. 
The program runs continuously using a background thread and stores the data for later analysis.

## Features
- Monitors CPU usage, physical memory, and JVM memory
- Uses multithreading for continuous background monitoring
- Stores system data in a CSV file
- Analyzes data to find minimum, maximum, and average usage
- Detects sudden spikes in resource consumption

## Project Structure
SystemMonitor/
- ProfessionalMonitor.java   // Collects and logs system resource data
- LogAnalyzer.java           // Analyzes CSV data and generates summary
- system_stats.csv           // Stored monitoring data

## How It Works
1. ProfessionalMonitor collects system statistics at regular intervals.
2. The data is saved to system_stats.csv.
3. LogAnalyzer reads the file and calculates performance metrics.

## Technologies Used
- Java
- Multithreading
- File Handling (CSV)
- System Management APIs

## How to Run

Compile:
javac SystemMonitor/*.java

Run Monitor:
java SystemMonitor.ProfessionalMonitor

Run Analyzer:
java SystemMonitor.LogAnalyzer

## Purpose
This project was built to practice multithreading, system monitoring, and file handling in Java.
