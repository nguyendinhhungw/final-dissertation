package com.merryblue.api.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class ProfileDTO {
    private UUID id;
    private UUID userId;
    private String displayName;
    private String avatarUrl;
    private String phone;
    private OffsetDateTime createdAt;
}
