package com.merryblue.api.service;

import com.merryblue.api.dto.request.ContactCreateRequest;
import com.merryblue.api.model.JobApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Service dedicated to identifying and preventing fraudulent or spammy submissions
 * in forms like Job Applications and Contact Us.
 * Uses a combination of velocity tracking, pattern matching, and heuristic scoring.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    // In-memory velocity tracking (in a real app, use Redis)
    private final Map<String, VelocityRecord> ipVelocityMap = new ConcurrentHashMap<>();
    
    // Thresholds
    private static final int MAX_REQUESTS_PER_HOUR = 5;
    private static final int SPAM_SCORE_THRESHOLD = 70;

    // Known spam indicators
    private static final Set<String> SPAM_KEYWORDS = Set.of(
            "seo services", "buy cheap", "lottery", "crypto investment",
            "enlarge", "viagra", "million dollars", "prince of nigeria", "click here to win"
    );

    private static final Pattern URL_PATTERN = Pattern.compile("https?://(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");

    /**
     * Evaluates a contact submission for spam/fraud.
     * Throws an exception if blocked, or returns a score for administrative review.
     */
    public FraudEvaluationResult evaluateContactSubmission(ContactCreateRequest request, String clientIp) {
        log.info("Starting fraud evaluation for contact submission from IP: {}", clientIp);

        // 1. Check Velocity (Rate Limiting)
        if (isRateLimited(clientIp)) {
            log.warn("IP {} is rate limited due to high velocity.", clientIp);
            return new FraudEvaluationResult(true, 100, List.of("Velocity limit exceeded (Too many requests from this IP)."));
        }

        int spamScore = 0;
        List<String> flags = new ArrayList<>();

        // 2. Keyword Analysis
        String content = (request.getSubject() + " " + request.getMessage()).toLowerCase();
        int keywordMatches = 0;
        for (String keyword : SPAM_KEYWORDS) {
            if (content.contains(keyword)) {
                keywordMatches++;
                flags.add("Contains spam keyword: '" + keyword + "'");
            }
        }
        spamScore += (keywordMatches * 30); // Heavy penalty for spam keywords

        // 3. Link Density Analysis
        int linkCount = 0;
        var matcher = URL_PATTERN.matcher(content);
        while (matcher.find()) linkCount++;
        
        if (linkCount > 2) {
            spamScore += 40;
            flags.add("High link density detected (" + linkCount + " links).");
        } else if (linkCount > 0) {
            spamScore += 10;
        }

        // 4. Text Structure Heuristics (e.g., all caps)
        if (isMostlyUpperCase(request.getMessage())) {
            spamScore += 20;
            flags.add("Message body is mostly uppercase.");
        }

        // 5. Gibberish Detection (Very basic mock: excessive consonants)
        if (containsGibberish(request.getName()) || containsGibberish(request.getSubject())) {
            spamScore += 50;
            flags.add("Name or Subject appears to be auto-generated gibberish.");
        }

        boolean isBlocked = spamScore >= SPAM_SCORE_THRESHOLD;
        
        if (isBlocked) {
            log.warn("Blocked submission from IP {}. Score: {}. Flags: {}", clientIp, spamScore, flags);
        } else {
            recordAction(clientIp); // Only record successful non-blocked submissions for rate limiting
        }

        return new FraudEvaluationResult(isBlocked, spamScore, flags);
    }

    /**
     * Evaluates a job application for potential automated spam or fake data.
     */
    public FraudEvaluationResult evaluateJobApplication(JobApplication application, String clientIp) {
        log.info("Starting fraud evaluation for Job Application from IP: {}", clientIp);
        
        if (isRateLimited(clientIp)) {
            return new FraudEvaluationResult(true, 100, List.of("Velocity limit exceeded."));
        }

        int spamScore = 0;
        List<String> flags = new ArrayList<>();

        // Fake Email Check (Basic)
        if (application.getEmail() != null && application.getEmail().endsWith("@test.com")) {
            spamScore += 80;
            flags.add("Suspicious email domain (@test.com).");
        }

        // Extremely short cover letter
        if (application.getCoverLetter() != null && application.getCoverLetter().length() < 20) {
            spamScore += 30;
            flags.add("Cover letter is abnormally short.");
        }

        boolean isBlocked = spamScore >= SPAM_SCORE_THRESHOLD;
        if (!isBlocked) recordAction(clientIp);

        return new FraudEvaluationResult(isBlocked, spamScore, flags);
    }

    // --- Private Helper Methods ---

    private boolean isRateLimited(String ip) {
        VelocityRecord record = ipVelocityMap.get(ip);
        if (record == null) return false;
        
        // Reset window if an hour has passed
        if (System.currentTimeMillis() - record.windowStartTime > 3600000) {
            ipVelocityMap.remove(ip);
            return false;
        }
        
        return record.requestCount.get() >= MAX_REQUESTS_PER_HOUR;
    }

    private void recordAction(String ip) {
        ipVelocityMap.compute(ip, (k, v) -> {
            if (v == null || System.currentTimeMillis() - v.windowStartTime > 3600000) {
                return new VelocityRecord();
            }
            v.requestCount.incrementAndGet();
            return v;
        });
    }

    private boolean isMostlyUpperCase(String text) {
        if (text == null || text.length() < 10) return false;
        long upperCount = text.chars().filter(Character::isUpperCase).count();
        long alphaCount = text.chars().filter(Character::isLetter).count();
        if (alphaCount == 0) return false;
        return ((double) upperCount / alphaCount) > 0.8;
    }

    private boolean containsGibberish(String text) {
        if (text == null) return false;
        // Mock logic: 5 consecutive consonants
        Pattern pattern = Pattern.compile("[bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ]{5,}");
        return pattern.matcher(text).find();
    }

    // --- Inner Classes ---

    private static class VelocityRecord {
        long windowStartTime = System.currentTimeMillis();
        AtomicInteger requestCount = new AtomicInteger(1);
    }

    public record FraudEvaluationResult(boolean isBlocked, int spamScore, List<String> flags) {
    }
}
