package com.merryblue.api.aspect;

import com.merryblue.api.exception.BadRequestException;
import com.merryblue.api.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class SecurityAspect {

    @Before("@annotation(com.merryblue.api.aspect.AdminOnly)")
    public void checkAdminRole() {
        log.info("AOP Security check: Validating admin role...");
        // Logic to check if current user has ADMIN role
        // For demonstration, we just log it
        SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BadRequestException("Unauthorized: No user found in context"));
    }
}
