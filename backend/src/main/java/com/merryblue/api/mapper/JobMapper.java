package com.merryblue.api.mapper;

import com.merryblue.api.dto.JobDTO;
import com.merryblue.api.model.Job;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public JobDTO toDTO(Job job) {
        if (job == null) return null;
        JobDTO dto = new JobDTO();
        dto.setId(job.getId());
        dto.setSlug(job.getSlug());
        dto.setTitleVi(job.getTitleVi());
        dto.setTitleEn(job.getTitleEn());
        dto.setDepartment(job.getDepartment());
        dto.setLocation(job.getLocation());
        dto.setEmploymentType(job.getEmploymentType());
        dto.setSalaryRange(job.getSalaryRange());
        dto.setShortVi(job.getShortVi());
        dto.setShortEn(job.getShortEn());
        dto.setDescriptionVi(job.getDescriptionVi());
        dto.setDescriptionEn(job.getDescriptionEn());
        dto.setRequirementsVi(job.getRequirementsVi());
        dto.setRequirementsEn(job.getRequirementsEn());
        dto.setBenefitsVi(job.getBenefitsVi());
        dto.setBenefitsEn(job.getBenefitsEn());
        dto.setIsOpen(job.getIsOpen());
        dto.setDisplayOrder(job.getDisplayOrder());
        dto.setCreatedAt(job.getCreatedAt());
        return dto;
    }

    public Job toEntity(JobDTO dto) {
        if (dto == null) return null;
        Job job = new Job();
        job.setId(dto.getId());
        job.setSlug(dto.getSlug());
        job.setTitleVi(dto.getTitleVi());
        job.setTitleEn(dto.getTitleEn());
        job.setDepartment(dto.getDepartment());
        job.setLocation(dto.getLocation());
        job.setEmploymentType(dto.getEmploymentType());
        job.setSalaryRange(dto.getSalaryRange());
        job.setShortVi(dto.getShortVi());
        job.setShortEn(dto.getShortEn());
        job.setDescriptionVi(dto.getDescriptionVi());
        job.setDescriptionEn(dto.getDescriptionEn());
        job.setRequirementsVi(dto.getRequirementsVi());
        job.setRequirementsEn(dto.getRequirementsEn());
        job.setBenefitsVi(dto.getBenefitsVi());
        job.setBenefitsEn(dto.getBenefitsEn());
        job.setIsOpen(dto.getIsOpen());
        job.setDisplayOrder(dto.getDisplayOrder());
        job.setCreatedAt(dto.getCreatedAt());
        return job;
    }
}
