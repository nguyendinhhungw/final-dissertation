package com.merryblue.api.controller;

import com.merryblue.api.dto.ApiResponse;
import com.merryblue.api.dto.ProfileDTO;
import com.merryblue.api.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ApiResponse<ProfileDTO> getMyProfile() {
        return ApiResponse.success(profileService.getCurrentUserProfile());
    }

    @PutMapping
    public ApiResponse<ProfileDTO> updateProfile(@RequestBody ProfileDTO profileDTO) {
        return ApiResponse.success(profileService.updateProfile(profileDTO));
    }
}
