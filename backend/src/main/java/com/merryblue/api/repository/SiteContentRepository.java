package com.merryblue.api.repository;

import com.merryblue.api.model.SiteContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SiteContentRepository extends JpaRepository<SiteContent, UUID> {
    Optional<SiteContent> findByKey(String key);
}
