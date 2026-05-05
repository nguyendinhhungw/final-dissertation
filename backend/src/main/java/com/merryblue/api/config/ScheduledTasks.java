package com.merryblue.api.config;

import com.merryblue.api.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final ActivityLogRepository activityLogRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void cleanOldLogs() {
        log.info("Cleaning up activity logs older than 30 days...");
        // Logic to delete old logs
    }

    @Scheduled(fixedRate = 3600000) // Every hour
    public void generateHourlyReport() {
        log.info("Generating hourly system status report...");
    }
}
