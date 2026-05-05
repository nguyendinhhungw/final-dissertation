package com.merryblue.api.dto.request;

import com.merryblue.api.model.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationUpdateRequest {
    private ApplicationStatus status;
}
