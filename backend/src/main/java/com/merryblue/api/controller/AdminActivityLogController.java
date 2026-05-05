package com.merryblue.api.controller;

import com.merryblue.api.dto.ApiResponse;
import com.merryblue.api.model.ActivityLog;
import com.merryblue.api.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class AdminActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public ApiResponse<List<ActivityLog>> getLogs() {
        return ApiResponse.success(activityLogService.getAllLogs());
    }
}
