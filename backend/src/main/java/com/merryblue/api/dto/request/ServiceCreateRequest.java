package com.merryblue.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServiceCreateRequest {
    @NotBlank
    private String titleVi;
    private String titleEn;
    @NotBlank
    private String descriptionVi;
    private String descriptionEn;
    private String icon;
}
