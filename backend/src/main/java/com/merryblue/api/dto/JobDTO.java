package com.merryblue.api.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class JobDTO {
    private UUID id;
    private String slug;
    private String titleVi;
    private String titleEn;
    private String department;
    private String location;
    private String employmentType;
    private String salaryRange;
    private String shortVi;
    private String shortEn;
    private String descriptionVi;
    private String descriptionEn;
    private String requirementsVi;
    private String requirementsEn;
    private String benefitsVi;
    private String benefitsEn;
    private Boolean isOpen;
    private Integer displayOrder;
    private OffsetDateTime createdAt;
}
