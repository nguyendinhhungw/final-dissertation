package com.merryblue.api.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class JobApplicationDTO {
    private UUID id;
    private UUID userId;
    private UUID jobId;
    private String jobTitleVi;
    private String jobTitleEn;
    private String fullName;
    private String email;
    private String phone;
    private String cvPath;
    private String coverLetter;
    private String status;
    private String adminNotes;
    private OffsetDateTime createdAt;
}
