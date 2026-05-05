package com.merryblue.api.controller;

import com.merryblue.api.dto.ApiResponse;
import com.merryblue.api.dto.SystemHealthDTO;
import com.merryblue.api.service.SystemHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemHealthService systemHealthService;

    @GetMapping("/health")
    public ApiResponse<SystemHealthDTO> getHealth() {
        return ApiResponse.success(systemHealthService.getHealthStatus());
    }
}
