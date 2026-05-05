package com.merryblue.api.service;

import com.merryblue.api.dto.UserRoleDTO;
import com.merryblue.api.mapper.UserRoleMapper;
import com.merryblue.api.model.AppRole;
import com.merryblue.api.model.UserRole;
import com.merryblue.api.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRoleMapper userRoleMapper;

    public List<UserRoleDTO> getRolesByUserId(UUID userId) {
        return userRoleRepository.findByUserId(userId)
                .stream().map(userRoleMapper::toDTO).collect(Collectors.toList());
    }

    public void assignRole(UUID userId, AppRole roleName) {
        UserRole role = new UserRole();
        role.setUserId(userId);
        role.setRole(roleName);
        userRoleRepository.save(role);
    }
}
