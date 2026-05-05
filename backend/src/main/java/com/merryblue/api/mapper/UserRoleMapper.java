package com.merryblue.api.mapper;

import com.merryblue.api.dto.UserRoleDTO;
import com.merryblue.api.model.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserRoleMapper {

    public UserRoleDTO toDTO(UserRole userRole) {
        if (userRole == null) return null;
        UserRoleDTO dto = new UserRoleDTO();
        dto.setId(userRole.getId());
        dto.setUserId(userRole.getUserId());
        dto.setRole(userRole.getRole());
        dto.setCreatedAt(userRole.getCreatedAt());
        return dto;
    }

    public UserRole toEntity(UserRoleDTO dto) {
        if (dto == null) return null;
        UserRole userRole = new UserRole();
        userRole.setId(dto.getId());
        userRole.setUserId(dto.getUserId());
        userRole.setRole(dto.getRole());
        userRole.setCreatedAt(dto.getCreatedAt());
        return userRole;
    }
}
