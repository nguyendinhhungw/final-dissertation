package com.merryblue.api.controller;

import com.merryblue.api.dto.ApiResponse;
import com.merryblue.api.dto.DashboardDTO;
import com.merryblue.api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ApiResponse<DashboardDTO> getStats() {
        return ApiResponse.success(dashboardService.getStats());
    }
}
