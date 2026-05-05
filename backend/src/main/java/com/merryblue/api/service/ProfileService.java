package com.merryblue.api.service;

import com.merryblue.api.dto.ProfileDTO;
import com.merryblue.api.mapper.ProfileMapper;
import com.merryblue.api.model.Profile;
import com.merryblue.api.repository.ProfileRepository;
import com.merryblue.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    public ProfileDTO getProfileByUserId(UUID userId) {
        return profileRepository.findByUserId(userId)
                .map(profileMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    public Profile getProfileEntityByUserId(UUID userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    @Transactional
    public ProfileDTO updateProfile(UUID userId, Profile updated) {
        Profile profile = getProfileEntityByUserId(userId);
        profile.setDisplayName(updated.getDisplayName());
        profile.setAvatarUrl(updated.getAvatarUrl());
        profile.setPhone(updated.getPhone());
        return profileMapper.toDTO(profileRepository.save(profile));
    }
}
