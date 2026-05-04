package com.merryblue.api.repository;

import com.merryblue.api.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {
    List<JobApplication> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<JobApplication> findAllByOrderByCreatedAtDesc();
}
