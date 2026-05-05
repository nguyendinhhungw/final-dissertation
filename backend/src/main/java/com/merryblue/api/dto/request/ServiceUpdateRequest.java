package com.merryblue.api.dto.request;

import lombok.Data;

@Data
public class ServiceUpdateRequest {
    private String titleVi;
    private String titleEn;
    private String descriptionVi;
    private String descriptionEn;
    private String icon;
}
