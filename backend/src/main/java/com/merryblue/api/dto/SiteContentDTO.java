package com.merryblue.api.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class SiteContentDTO {
    private UUID id;
    private String key;
    private String valueVi;
    private String valueEn;
    private String description;
    private OffsetDateTime updatedAt;
}
