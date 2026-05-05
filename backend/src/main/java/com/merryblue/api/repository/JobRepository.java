package com.merryblue.api.repository;

import com.merryblue.api.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Job> {
    Optional<Job> findBySlug(String slug);
    List<Job> findByIsOpenTrueOrderByDisplayOrderAsc();
    List<Job> findAllByOrderByDisplayOrderAsc();
}
