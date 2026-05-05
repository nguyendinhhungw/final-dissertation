package com.merryblue.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemHealthDTO {
    private String status;
    private long freeDiskSpace;
    private long totalDiskSpace;
    private String databaseConnection;
    private long uptime;
}
