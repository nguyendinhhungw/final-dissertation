package com.merryblue.api.service;

import com.merryblue.api.dto.PortfolioProjectDTO;
import com.merryblue.api.mapper.PortfolioMapper;
import com.merryblue.api.model.PortfolioProject;
import com.merryblue.api.repository.PortfolioProjectRepository;
import com.merryblue.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioProjectRepository portfolioProjectRepository;
    private final PortfolioMapper portfolioMapper;

    public List<PortfolioProjectDTO> getAllProjects(boolean onlyPublished) {
        List<PortfolioProject> projects;
        if (onlyPublished) {
            projects = portfolioProjectRepository.findByIsPublishedTrueOrderByDisplayOrderAsc();
        } else {
            projects = portfolioProjectRepository.findAllByOrderByDisplayOrderAsc();
        }
        return projects.stream().map(portfolioMapper::toDTO).collect(Collectors.toList());
    }

    public PortfolioProjectDTO getProjectBySlug(String slug) {
        return portfolioProjectRepository.findBySlug(slug)
                .map(portfolioMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio project not found"));
    }

    public PortfolioProject getProjectEntityById(UUID id) {
        return portfolioProjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio project not found"));
    }

    @Transactional
    public PortfolioProjectDTO createProject(PortfolioProjectDTO projectDTO) {
        PortfolioProject project = portfolioMapper.toEntity(projectDTO);
        return portfolioMapper.toDTO(portfolioProjectRepository.save(project));
    }

    @Transactional
    public PortfolioProjectDTO updateProject(UUID id, PortfolioProjectDTO updatedDTO) {
        PortfolioProject existing = getProjectEntityById(id);
        PortfolioProject updated = portfolioMapper.toEntity(updatedDTO);
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        return portfolioMapper.toDTO(portfolioProjectRepository.save(updated));
    }

    @Transactional
    public void deleteProject(UUID id) {
        portfolioProjectRepository.deleteById(id);
    }
}
