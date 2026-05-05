package com.merryblue.api.dto;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class AuditLogDTO {
    private String action;
    private OffsetDateTime timestamp;
}
