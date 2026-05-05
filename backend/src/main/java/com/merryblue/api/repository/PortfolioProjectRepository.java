package com.merryblue.api.repository;

import com.merryblue.api.model.PortfolioProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PortfolioProjectRepository extends JpaRepository<PortfolioProject, UUID>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<PortfolioProject> {
    Optional<PortfolioProject> findBySlug(String slug);
    List<PortfolioProject> findByIsPublishedTrueOrderByDisplayOrderAsc();
    List<PortfolioProject> findAllByOrderByDisplayOrderAsc();
}
