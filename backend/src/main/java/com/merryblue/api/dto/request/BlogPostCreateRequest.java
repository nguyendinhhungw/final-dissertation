package com.merryblue.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlogPostCreateRequest {
    @NotBlank
    private String titleVi;
    private String titleEn;
    @NotBlank
    private String contentVi;
    private String contentEn;
    private String slug;
    private String imageUrl;
    private Boolean isPublished;
}
