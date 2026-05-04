package com.merryblue.api.repository;

import com.merryblue.api.model.UserRole;
import com.merryblue.api.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    List<UserRole> findByUserId(UUID userId);
    Optional<UserRole> findByUserIdAndRole(UUID userId, AppRole role);
}
