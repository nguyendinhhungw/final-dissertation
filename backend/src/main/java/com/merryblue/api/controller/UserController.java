package com.merryblue.api.controller;

import com.merryblue.api.dto.ProfileDTO;
import com.merryblue.api.dto.NotificationDTO;
import com.merryblue.api.model.Profile;
import com.merryblue.api.service.ProfileService;
import com.merryblue.api.service.NotificationService;
import com.merryblue.api.security.MerryblueUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class UserController {

    private final ProfileService profileService;
    private final NotificationService notificationService;

    @GetMapping("/profile")
    public ProfileDTO getMyProfile(@AuthenticationPrincipal MerryblueUserPrincipal principal) {
        UUID userId = UUID.fromString(principal.getId());
        return profileService.getProfileByUserId(userId);
    }

    @PutMapping("/profile")
    public ProfileDTO updateMyProfile(
            @AuthenticationPrincipal MerryblueUserPrincipal principal,
            @RequestBody Profile updatedProfile) {
        UUID userId = UUID.fromString(principal.getId());
        return profileService.updateProfile(userId, updatedProfile);
    }

    @GetMapping("/notifications")
    public List<NotificationDTO> getMyNotifications(@AuthenticationPrincipal MerryblueUserPrincipal principal) {
        UUID userId = UUID.fromString(principal.getId());
        return notificationService.getNotificationsByUserId(userId);
    }

    @PutMapping("/notifications/read-all")
    public void markAllNotificationsAsRead(@AuthenticationPrincipal MerryblueUserPrincipal principal) {
        UUID userId = UUID.fromString(principal.getId());
        notificationService.markAllAsRead(userId);
    }
}
