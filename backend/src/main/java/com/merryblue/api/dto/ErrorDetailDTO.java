package com.merryblue.api.dto;

import lombok.Data;

@Data
public class ErrorDetailDTO {
    private String field;
    private String message;
}
