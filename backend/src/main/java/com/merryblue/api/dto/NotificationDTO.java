package com.merryblue.api.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class NotificationDTO {
    private UUID id;
    private UUID userId;
    private String title;
    private String message;
    private String link;
    private Boolean isRead;
    private OffsetDateTime createdAt;
}
