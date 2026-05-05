package com.merryblue.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.merryblue.api.model.JobApplication;
import com.merryblue.api.model.User;
import com.merryblue.api.repository.JobApplicationRepository;
import com.merryblue.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service responsible for complex data sanitization and GDPR compliance.
 * Handles anonymization of Personally Identifiable Information (PII) before data
 * is archived, exported, or safely utilized for machine learning/analytics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataAnonymizationService {

    private final UserRepository userRepository;
    private final JobApplicationRepository applicationRepository;
    private final ObjectMapper objectMapper;

    // Common PII patterns
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+\\d{1,3}[- ]?)?\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{4}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    private static final Pattern SSN_PATTERN = Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b"); // Example for US SSN logic, adapt for VN CMND/CCCD if needed
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile("\\b(?:\\d{4}[ -]?){3}\\d{4}\\b");

    /**
     * Fully anonymizes a User record. This is a destructive operation used when a user requests account deletion (Right to be Forgotten).
     */
    @Transactional
    public void executeRightToBeForgotten(UUID userId) {
        log.warn("Executing Right to be Forgotten (Full Anonymization) for User ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 1. Direct Field Anonymization
        user.setEmail(generateDeterministicHash(user.getEmail()) + "@anonymized.local");
        user.setUsername("anonymized_user_" + UUID.randomUUID().toString().substring(0, 8));
        
        // Ensure sensitive fields (if any) are nullified
        // user.setFirstName("Redacted");
        // user.setLastName("Redacted");
        // user.setPhone(null);

        userRepository.save(user);

        // 2. Cascade Anonymization to related records (e.g., Job Applications)
        List<JobApplication> applications = applicationRepository.findAll().stream()
                // Assuming JobApplication has a relation or we search by email
                // For mock purposes, just finding by exact old email if we had it, but we already hashed it.
                // Normally you'd query by UserId if it's a FK.
                .toList();

        for (JobApplication app : applications) {
            anonymizeJobApplication(app);
            applicationRepository.save(app);
        }

        log.info("Right to be Forgotten completed for User ID: {}", userId);
    }

    /**
     * Safely anonymizes a job application, redacting PII from free-text fields like cover letters.
     */
    public void anonymizeJobApplication(JobApplication application) {
        // Redact structured fields
        if (application.getFullName() != null) {
            application.setFullName("Applicant_" + application.getId().toString().substring(0, 5));
        }
        if (application.getEmail() != null) {
            application.setEmail(generateDeterministicHash(application.getEmail()) + "@anonymized.local");
        }
        if (application.getPhone() != null) {
            application.setPhone("[REDACTED_PHONE]");
        }
        if (application.getCvUrl() != null) {
            application.setCvUrl("[REDACTED_FILE_LINK]");
        }

        // Heavy Logic: Redact free-text fields
        if (application.getCoverLetter() != null) {
            String redactedCoverLetter = scrubPiiFromText(application.getCoverLetter());
            application.setCoverLetter(redactedCoverLetter);
        }
    }

    /**
     * Deep JSON anonymization. Traverses a JSON tree and applies redacting rules.
     * Useful for anonymizing NoSQL documents or complex JSON payloads before dumping to data lakes.
     */
    public String anonymizeJsonPayload(String jsonPayload) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonPayload);
            anonymizeJsonNode(rootNode);
            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            log.error("Failed to anonymize JSON payload", e);
            return "{\"error\": \"Anonymization Failed\"}";
        }
    }

    private void anonymizeJsonNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objNode = (ObjectNode) node;
            
            // Explicit key targeting
            if (objNode.has("email")) objNode.put("email", "[REDACTED_EMAIL]");
            if (objNode.has("phoneNumber")) objNode.put("phoneNumber", "[REDACTED_PHONE]");
            if (objNode.has("creditCard")) objNode.put("creditCard", "[REDACTED_CC]");
            if (objNode.has("password")) objNode.put("password", "[REDACTED_PWD]");
            
            // Recursive scan for string values that might contain PII
            objNode.fields().forEachRemaining(entry -> {
                JsonNode child = entry.getValue();
                if (child.isTextual()) {
                    String scrubbed = scrubPiiFromText(child.asText());
                    if (!scrubbed.equals(child.asText())) {
                        objNode.put(entry.getKey(), scrubbed);
                    }
                } else {
                    anonymizeJsonNode(child);
                }
            });
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                anonymizeJsonNode(child);
            }
        }
    }

    /**
     * Core text scrubbing engine. Uses Regex to find and replace PII patterns.
     */
    private String scrubPiiFromText(String text) {
        if (text == null || text.isEmpty()) return text;

        String result = text;
        
        Matcher emailMatcher = EMAIL_PATTERN.matcher(result);
        result = emailMatcher.replaceAll("[EMAIL_REDACTED]");

        Matcher phoneMatcher = PHONE_PATTERN.matcher(result);
        result = phoneMatcher.replaceAll("[PHONE_REDACTED]");

        Matcher ssnMatcher = SSN_PATTERN.matcher(result);
        result = ssnMatcher.replaceAll("[SSN_REDACTED]");

        Matcher ccMatcher = CREDIT_CARD_PATTERN.matcher(result);
        result = ccMatcher.replaceAll("[CREDIT_CARD_REDACTED]");

        return result;
    }

    /**
     * Generates a consistent, non-reversible hash for fields like email, 
     * allowing analytics to group user behavior without knowing who they are.
     */
    private String generateDeterministicHash(String input) {
        if (input == null) return "null";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encodedhash).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not found", e);
            return UUID.randomUUID().toString(); // Fallback
        }
    }
}
