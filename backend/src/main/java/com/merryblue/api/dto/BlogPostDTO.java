package com.merryblue.api.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class BlogPostDTO {
    private UUID id;
    private String slug;
    private String titleVi;
    private String titleEn;
    private String excerptVi;
    private String excerptEn;
    private String bodyVi;
    private String bodyEn;
    private String coverUrl;
    private String category;
    private List<String> tags;
    private String author;
    private Boolean isPublished;
    private Boolean isFeatured;
    private Integer views;
    private Integer displayOrder;
    private OffsetDateTime publishedAt;
    private OffsetDateTime createdAt;
}
