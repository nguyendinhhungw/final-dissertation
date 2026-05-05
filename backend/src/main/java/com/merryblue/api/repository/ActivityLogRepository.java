package com.merryblue.api.repository;

import com.merryblue.api.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    List<ActivityLog> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<ActivityLog> findAllByOrderByCreatedAtDesc();
}
