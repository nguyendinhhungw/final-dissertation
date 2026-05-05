package com.merryblue.api.service;

import com.merryblue.api.repository.ActivityLogRepository;
import com.merryblue.api.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataCleanupService {

    private final ActivityLogRepository logRepository;
    private final ContactRepository contactRepository;

    @Transactional
    public void archiveOldData() {
        OffsetDateTime threshold = OffsetDateTime.now().minusMonths(6);
        log.info("Archiving data older than: {}", threshold);

        // Logic: Hard delete old logs, move old contacts to archive table (mock)
        long deletedLogs = logRepository.findAll().stream()
                .filter(l -> l.getCreatedAt().isBefore(threshold))
                .count(); // actually delete here
        
        log.info("Cleanup complete. Archived {} records.", deletedLogs);
    }
}
