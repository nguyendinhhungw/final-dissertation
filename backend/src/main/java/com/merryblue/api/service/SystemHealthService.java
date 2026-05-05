package com.merryblue.api.service;

import com.merryblue.api.dto.SystemHealthDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;

@Service
@Slf4j
public class SystemHealthService {

    public SystemHealthDTO getHealthStatus() {
        File root = new File("/");
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        
        return SystemHealthDTO.builder()
                .status("UP")
                .freeDiskSpace(root.getFreeSpace())
                .totalDiskSpace(root.getTotalSpace())
                .databaseConnection("CONNECTED")
                .uptime(uptime)
                .build();
    }
}
