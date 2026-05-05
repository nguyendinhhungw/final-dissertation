package com.merryblue.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.management.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Highly complex service responsible for aggregating deep system telemetry data.
 * It queries JVM beans, OS-level metrics, and Database health to create a unified 
 * time-series representation of system health for monitoring dashboards.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemHealthTelemetryService {

    private final DataSource dataSource;

    // Cache of history to simulate time-series in memory
    private final List<Map<String, Object>> telemetryHistory = new ArrayList<>();
    private static final int MAX_HISTORY_POINTS = 1440; // E.g., 24 hours of minute-by-minute data

    /**
     * Captures a comprehensive snapshot of system health right now.
     * Includes JVM memory, threading, GC stats, OS load, and DB connectivity ping.
     */
    public Map<String, Object> captureFullTelemetrySnapshot() {
        log.debug("Capturing full system telemetry snapshot...");
        long startTime = System.currentTimeMillis();

        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("timestamp", OffsetDateTime.now());

        // 1. Host Information
        try {
            InetAddress ip = InetAddress.getLocalHost();
            snapshot.put("hostName", ip.getHostName());
            snapshot.put("hostIp", ip.getHostAddress());
        } catch (UnknownHostException e) {
            snapshot.put("hostName", "UNKNOWN");
        }

        // 2. OS & CPU Metrics
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        snapshot.put("osName", osBean.getName());
        snapshot.put("osVersion", osBean.getVersion());
        snapshot.put("osArch", osBean.getArch());
        snapshot.put("availableProcessors", osBean.getAvailableProcessors());
        snapshot.put("systemLoadAverage", osBean.getSystemLoadAverage());

        // 3. JVM Memory Metrics
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        
        Map<String, Long> memoryStats = new HashMap<>();
        memoryStats.put("heapInit", heapUsage.getInit());
        memoryStats.put("heapUsed", heapUsage.getUsed());
        memoryStats.put("heapCommitted", heapUsage.getCommitted());
        memoryStats.put("heapMax", heapUsage.getMax());
        memoryStats.put("nonHeapUsed", nonHeapUsage.getUsed());
        snapshot.put("memory", memoryStats);

        // Calculate Heap Utilization Percentage
        if (heapUsage.getMax() > 0) {
            double utilization = ((double) heapUsage.getUsed() / heapUsage.getMax()) * 100;
            snapshot.put("heapUtilizationPercent", utilization);
            
            // Alerting Logic
            if (utilization > 85.0) {
                log.warn("ALERT: High heap memory utilization detected: {}%", String.format("%.2f", utilization));
                snapshot.put("memoryAlertState", "CRITICAL");
            } else if (utilization > 70.0) {
                snapshot.put("memoryAlertState", "WARNING");
            } else {
                snapshot.put("memoryAlertState", "HEALTHY");
            }
        }

        // 4. Thread Metrics
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Map<String, Integer> threadStats = new HashMap<>();
        threadStats.put("threadCount", threadBean.getThreadCount());
        threadStats.put("peakThreadCount", threadBean.getPeakThreadCount());
        threadStats.put("daemonThreadCount", threadBean.getDaemonThreadCount());
        
        // Find deadlocked threads
        long[] deadlockedThreads = threadBean.findDeadlockedThreads();
        boolean isDeadlocked = deadlockedThreads != null && deadlockedThreads.length > 0;
        threadStats.put("deadlockedThreadCount", isDeadlocked ? deadlockedThreads.length : 0);
        snapshot.put("threads", threadStats);

        if (isDeadlocked) {
            log.error("CRITICAL ALERT: JVM Deadlock detected involving {} threads!", deadlockedThreads.length);
        }

        // 5. Garbage Collection Metrics
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        long totalGcCollectionCount = 0;
        long totalGcCollectionTime = 0;
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            totalGcCollectionCount += gcBean.getCollectionCount();
            totalGcCollectionTime += gcBean.getCollectionTime();
        }
        Map<String, Long> gcStats = new HashMap<>();
        gcStats.put("totalGcCount", totalGcCollectionCount);
        gcStats.put("totalGcTimeMs", totalGcCollectionTime);
        snapshot.put("garbageCollection", gcStats);

        // 6. Database Health Check (Active Ping)
        long dbPingStart = System.currentTimeMillis();
        boolean dbHealthy = checkDatabaseConnectivity();
        long dbPingLatency = System.currentTimeMillis() - dbPingStart;
        
        Map<String, Object> dbStats = new HashMap<>();
        dbStats.put("isHealthy", dbHealthy);
        dbStats.put("pingLatencyMs", dbPingLatency);
        
        if (!dbHealthy) {
            log.error("CRITICAL ALERT: Database connectivity check failed!");
            dbStats.put("alertState", "CRITICAL_OFFLINE");
        } else if (dbPingLatency > 500) {
            log.warn("Database response is slow. Ping latency: {} ms", dbPingLatency);
            dbStats.put("alertState", "WARNING_DEGRADED");
        } else {
            dbStats.put("alertState", "HEALTHY");
        }
        snapshot.put("database", dbStats);

        // 7. Overall System Health Aggregation
        boolean systemOverallHealthy = dbHealthy && !isDeadlocked && (heapUsage.getMax() == -1 || ((double) heapUsage.getUsed() / heapUsage.getMax()) < 0.90);
        snapshot.put("overallHealthStatus", systemOverallHealthy ? "UP" : "DOWN");

        // Record Execution Time
        snapshot.put("telemetryExecutionTimeMs", System.currentTimeMillis() - startTime);

        // Save to in-memory history
        saveToHistory(snapshot);

        return snapshot;
    }

    private boolean checkDatabaseConnectivity() {
        if (dataSource == null) return false;
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2); // 2 second timeout
        } catch (SQLException e) {
            log.error("SQL Exception during DB health ping", e);
            return false;
        }
    }

    private synchronized void saveToHistory(Map<String, Object> snapshot) {
        if (telemetryHistory.size() >= MAX_HISTORY_POINTS) {
            telemetryHistory.remove(0); // Remove oldest
        }
        telemetryHistory.add(snapshot);
    }

    /**
     * Retrieves the historical telemetry time-series data for dashboard rendering.
     */
    public List<Map<String, Object>> getTelemetryHistory() {
        return new ArrayList<>(telemetryHistory);
    }
}
