package com.merryblue.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merryblue.api.model.ActivityLog;
import com.merryblue.api.repository.ActivityLogRepository;
import com.merryblue.api.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Advanced auditing service capable of deep object inspection.
 * It compares previous and new states of entities to generate detailed, field-level diffs
 * for comprehensive compliance and tracking.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComprehensiveAuditTrailService {

    private final ActivityLogRepository activityLogRepository;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    /**
     * Captures a detailed change event between two object states.
     * Generates a precise JSON diff of what fields changed, from what old value to what new value.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void captureDetailedChangeEvent(String action, String resourceType, String resourceId, Object oldState, Object newState) {
        log.info("Capturing detailed audit event for {} - {}", resourceType, resourceId);

        UUID currentUserId = SecurityUtils.getCurrentUserId().orElse(null);
        String clientIp = getClientIpAddress();

        try {
            Map<String, Object> diffMap = generateObjectDiff(oldState, newState);
            
            if (diffMap.isEmpty() && oldState != null && newState != null) {
                log.debug("No differences detected between old and new state for {} - {}. Skipping audit log.", resourceType, resourceId);
                return;
            }

            String diffJson = objectMapper.writeValueAsString(diffMap);

            ActivityLog auditEntry = new ActivityLog();
            auditEntry.setUserId(currentUserId);
            auditEntry.setAction(action);
            auditEntry.setResourceType(resourceType);
            auditEntry.setResourceId(resourceId);
            auditEntry.setIpAddress(clientIp);
            auditEntry.setDetails(diffJson);

            activityLogRepository.save(auditEntry);
            log.info("Audit log successfully saved.");

        } catch (Exception e) {
            log.error("Failed to capture detailed audit event", e);
            // In a strict compliance system, you might want to throw here. 
            // For general apps, logging the error and continuing is safer to not break core flows.
        }
    }

    /**
     * Captures a simple event without a diff (e.g., READ, DELETE, or complex action invocations).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void captureSimpleEvent(String action, String resourceType, String resourceId, String additionalDetails) {
        UUID currentUserId = SecurityUtils.getCurrentUserId().orElse(null);
        String clientIp = getClientIpAddress();

        ActivityLog auditEntry = new ActivityLog();
        auditEntry.setUserId(currentUserId);
        auditEntry.setAction(action);
        auditEntry.setResourceType(resourceType);
        auditEntry.setResourceId(resourceId);
        auditEntry.setIpAddress(clientIp);
        auditEntry.setDetails(additionalDetails);

        activityLogRepository.save(auditEntry);
    }

    /**
     * Core logic: Deep reflection-based comparison of two objects to find changed fields.
     */
    private Map<String, Object> generateObjectDiff(Object oldObj, Object newObj) throws IllegalAccessException {
        Map<String, Object> differences = new HashMap<>();

        if (oldObj == null && newObj == null) {
            return differences;
        }

        if (oldObj == null) {
            differences.put("_changeType", "CREATE");
            differences.put("newState", serializeObjectSafely(newObj));
            return differences;
        }

        if (newObj == null) {
            differences.put("_changeType", "DELETE");
            differences.put("oldState", serializeObjectSafely(oldObj));
            return differences;
        }

        Class<?> clazz = oldObj.getClass();
        if (!clazz.equals(newObj.getClass())) {
            throw new IllegalArgumentException("Cannot compare objects of different classes");
        }

        differences.put("_changeType", "UPDATE");
        Map<String, Map<String, Object>> fieldChanges = new HashMap<>();

        // Walk up the class hierarchy to get all fields
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                
                // Skip specific fields that change naturally and shouldn't trigger an audit diff
                if (shouldSkipField(field.getName())) {
                    continue;
                }

                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);

                if (!Objects.equals(oldValue, newValue)) {
                    Map<String, Object> changeDetail = new HashMap<>();
                    changeDetail.put("old", oldValue);
                    changeDetail.put("new", newValue);
                    fieldChanges.put(field.getName(), changeDetail);
                }
            }
            clazz = clazz.getSuperclass();
        }

        if (!fieldChanges.isEmpty()) {
            differences.put("changes", fieldChanges);
        } else {
            // Remove UPDATE tag if nothing actually changed
            differences.clear();
        }

        return differences;
    }

    private boolean shouldSkipField(String fieldName) {
        Set<String> skipList = Set.of("updatedAt", "lastModifiedDate", "version", "id");
        return skipList.contains(fieldName);
    }

    private Map<String, Object> serializeObjectSafely(Object obj) {
        try {
            // Convert to a Map representation to avoid raw JSON string nesting
            String json = objectMapper.writeValueAsString(obj);
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize object for audit", e);
            return Map.of("error", "Unserializable object state");
        }
    }

    private String getClientIpAddress() {
        if (request == null) return "UNKNOWN";
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
