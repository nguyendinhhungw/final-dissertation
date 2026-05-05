package com.merryblue.api.service;

import com.merryblue.api.util.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserContextService {

    public UUID getCurrentUserId() {
        return SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("No user found in security context"));
    }

    public boolean isAuthenticated() {
        return SecurityUtils.getCurrentUserId().isPresent();
    }
}
