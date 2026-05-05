package com.merryblue.api.dto.request;

import com.merryblue.api.model.NotificationType;
import lombok.Data;
import java.util.UUID;

@Data
public class NotificationCreateRequest {
    private UUID userId;
    private String title;
    private String message;
    private NotificationType type;
}
