package com.merryblue.api.dto;

import com.merryblue.api.model.AppRole;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UserRoleDTO {
    private UUID id;
    private UUID userId;
    private AppRole role;
    private OffsetDateTime createdAt;
}
