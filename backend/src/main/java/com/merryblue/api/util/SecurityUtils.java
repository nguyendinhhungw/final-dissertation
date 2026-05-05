package com.merryblue.api.util;

import com.merryblue.api.security.MerryblueUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {

    public static Optional<UUID> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof MerryblueUserPrincipal) {
            return Optional.of(UUID.fromString(((MerryblueUserPrincipal) principal).getId()));
        }
        
        return Optional.empty();
    }

    private SecurityUtils() {
    }
}
