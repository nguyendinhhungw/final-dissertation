package com.merryblue.api.dto.request;

import lombok.Data;

@Data
public class PortfolioUpdateRequest {
    private String titleVi;
    private String titleEn;
    private String descriptionVi;
    private String descriptionEn;
    private String imageUrl;
    private String client;
    private String projectUrl;
}
