package com.merryblue.api.dto.request;

import lombok.Data;

@Data
public class JobUpdateRequest {
    private String titleVi;
    private String titleEn;
    private String descriptionVi;
    private String descriptionEn;
    private String location;
    private String salary;
    private Boolean isOpen;
}
