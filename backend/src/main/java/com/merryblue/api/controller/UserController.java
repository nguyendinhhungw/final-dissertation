package com.merryblue.api.controller;

import com.merryblue.api.model.Profile;
import com.merryblue.api.model.Notification;
import com.merryblue.api.repository.ProfileRepository;
import com.merryblue.api.repository.NotificationRepository;
import com.merryblue.api.security.MerryblueUserPrincipal;
import com.merryblue.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class UserController {

    private final ProfileRepository profileRepository;
    private final NotificationRepository notificationRepository;

    @GetMapping("/profile")
    public Profile getMyProfile(@AuthenticationPrincipal MerryblueUserPrincipal principal) {
        UUID userId = UUID.fromString(principal.getId());
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    @PutMapping("/profile")
    public Profile updateMyProfile(
            @AuthenticationPrincipal MerryblueUserPrincipal principal,
            @RequestBody Profile updatedProfile) {
        UUID userId = UUID.fromString(principal.getId());
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        
        profile.setDisplayName(updatedProfile.getDisplayName());
        profile.setAvatarUrl(updatedProfile.getAvatarUrl());
        profile.setPhone(updatedProfile.getPhone());
        
        return profileRepository.save(profile);
    }

    @GetMapping("/notifications")
    public List<Notification> getMyNotifications(@AuthenticationPrincipal MerryblueUserPrincipal principal) {
        UUID userId = UUID.fromString(principal.getId());
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PutMapping("/notifications/read-all")
    public void markAllNotificationsAsRead(@AuthenticationPrincipal MerryblueUserPrincipal principal) {
        UUID userId = UUID.fromString(principal.getId());
        List<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().filter(n -> !n.getIsRead()).toList();
        
        for (Notification n : unread) {
            n.setIsRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}
