package com.merryblue.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobCreateRequest {
    @NotBlank
    private String titleVi;
    private String titleEn;
    @NotBlank
    private String descriptionVi;
    private String descriptionEn;
    private String slug;
    private String location;
    private String salary;
    private Boolean isOpen;
}
