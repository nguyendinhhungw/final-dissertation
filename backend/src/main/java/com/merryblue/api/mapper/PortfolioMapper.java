package com.merryblue.api.mapper;

import com.merryblue.api.dto.PortfolioProjectDTO;
import com.merryblue.api.model.PortfolioProject;
import org.springframework.stereotype.Component;

@Component
public class PortfolioMapper {

    public PortfolioProjectDTO toDTO(PortfolioProject project) {
        if (project == null) return null;
        PortfolioProjectDTO dto = new PortfolioProjectDTO();
        dto.setId(project.getId());
        dto.setSlug(project.getSlug());
        dto.setTitleVi(project.getTitleVi());
        dto.setTitleEn(project.getTitleEn());
        dto.setShortVi(project.getShortVi());
        dto.setShortEn(project.getShortEn());
        dto.setBodyVi(project.getBodyVi());
        dto.setBodyEn(project.getBodyEn());
        dto.setCoverUrl(project.getCoverUrl());
        dto.setGallery(project.getGallery());
        dto.setTechStack(project.getTechStack());
        dto.setCategory(project.getCategory());
        dto.setDisplayOrder(project.getDisplayOrder());
        dto.setIsPublished(project.getIsPublished());
        dto.setCreatedAt(project.getCreatedAt());
        return dto;
    }

    public PortfolioProject toEntity(PortfolioProjectDTO dto) {
        if (dto == null) return null;
        PortfolioProject project = new PortfolioProject();
        project.setId(dto.getId());
        project.setSlug(dto.getSlug());
        project.setTitleVi(dto.getTitleVi());
        project.setTitleEn(dto.getTitleEn());
        project.setShortVi(dto.getShortVi());
        project.setShortEn(dto.getShortEn());
        project.setBodyVi(dto.getBodyVi());
        project.setBodyEn(dto.getBodyEn());
        project.setCoverUrl(dto.getCoverUrl());
        project.setGallery(dto.getGallery());
        project.setTechStack(dto.getTechStack());
        project.setCategory(dto.getCategory());
        project.setDisplayOrder(dto.getDisplayOrder());
        project.setIsPublished(dto.getIsPublished());
        project.setCreatedAt(dto.getCreatedAt());
        return project;
    }
}
