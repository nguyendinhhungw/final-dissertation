package com.merryblue.api.dto.request;

import lombok.Data;

@Data
public class BlogPostUpdateRequest {
    private String titleVi;
    private String titleEn;
    private String contentVi;
    private String contentEn;
    private String imageUrl;
    private Boolean isPublished;
}
