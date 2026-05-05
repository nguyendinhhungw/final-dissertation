package com.merryblue.api.mapper;

import com.merryblue.api.dto.ProfileDTO;
import com.merryblue.api.model.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileDTO toDTO(Profile profile) {
        if (profile == null) return null;
        ProfileDTO dto = new ProfileDTO();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUserId());
        dto.setDisplayName(profile.getDisplayName());
        dto.setAvatarUrl(profile.getAvatarUrl());
        dto.setPhone(profile.getPhone());
        dto.setCreatedAt(profile.getCreatedAt());
        return dto;
    }

    public Profile toEntity(ProfileDTO dto) {
        if (dto == null) return null;
        Profile profile = new Profile();
        profile.setId(dto.getId());
        profile.setUserId(dto.getUserId());
        profile.setDisplayName(dto.getDisplayName());
        profile.setAvatarUrl(dto.getAvatarUrl());
        profile.setPhone(dto.getPhone());
        profile.setCreatedAt(dto.getCreatedAt());
        return profile;
    }
}
