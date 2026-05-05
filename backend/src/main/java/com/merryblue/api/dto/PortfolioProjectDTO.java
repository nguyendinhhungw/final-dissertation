package com.merryblue.api.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PortfolioProjectDTO {
    private UUID id;
    private String slug;
    private String titleVi;
    private String titleEn;
    private String shortVi;
    private String shortEn;
    private String bodyVi;
    private String bodyEn;
    private String coverUrl;
    private String gallery;
    private List<String> techStack;
    private String category;
    private Integer displayOrder;
    private Boolean isPublished;
    private OffsetDateTime createdAt;
}
