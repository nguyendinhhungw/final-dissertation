package com.merryblue.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class ApplicationCreateRequest {
    private UUID jobId;
    @NotBlank
    private String fullName;
    @NotBlank
    private String email;
    private String phone;
    private String cvUrl;
    private String coverLetter;
}
