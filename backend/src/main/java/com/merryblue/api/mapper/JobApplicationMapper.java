package com.merryblue.api.mapper;

import com.merryblue.api.dto.JobApplicationDTO;
import com.merryblue.api.model.JobApplication;
import org.springframework.stereotype.Component;

@Component
public class JobApplicationMapper {

    public JobApplicationDTO toDTO(JobApplication app) {
        if (app == null) return null;
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setId(app.getId());
        dto.setUserId(app.getUserId());
        if (app.getJob() != null) {
            dto.setJobId(app.getJob().getId());
            dto.setJobTitleVi(app.getJob().getTitleVi());
            dto.setJobTitleEn(app.getJob().getTitleEn());
        }
        dto.setFullName(app.getFullName());
        dto.setEmail(app.getEmail());
        dto.setPhone(app.getPhone());
        dto.setCvPath(app.getCvPath());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setStatus(app.getStatus());
        dto.setAdminNotes(app.getAdminNotes());
        dto.setCreatedAt(app.getCreatedAt());
        return dto;
    }

    // Note: toEntity might need Job repository to fetch the job entity if only jobId is provided in DTO
}
