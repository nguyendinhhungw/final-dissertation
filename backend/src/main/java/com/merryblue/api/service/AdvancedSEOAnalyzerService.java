package com.merryblue.api.service;

import com.merryblue.api.model.BlogPost;
import com.merryblue.api.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service dedicated to analyzing and scoring the SEO (Search Engine Optimization)
 * quality of content within the Merryblue platform. This involves complex text processing,
 * readability scoring, and keyword density analysis.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdvancedSEOAnalyzerService {

    private final BlogPostRepository blogPostRepository;

    // Common stop words to ignore during keyword analysis
    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into",
            "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then",
            "there", "these", "they", "this", "to", "was", "will", "with"
    );

    /**
     * Performs a comprehensive SEO audit on a specific blog post.
     * Calculates an aggregate SEO score based on multiple heuristics.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> performComprehensiveSEOAudit(UUID postId, String targetKeyword) {
        log.info("Starting heavy SEO audit for post ID: {} with target keyword: '{}'", postId, targetKeyword);
        long startTime = System.currentTimeMillis();

        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found for SEO audit"));

        String content = post.getContentEn() != null ? post.getContentEn() : post.getContentVi();
        String title = post.getTitleEn() != null ? post.getTitleEn() : post.getTitleVi();

        if (content == null || content.trim().isEmpty()) {
            return Map.of("error", "Cannot analyze empty content", "score", 0);
        }

        Map<String, Object> auditResults = new HashMap<>();
        
        // 1. Keyword Density Analysis
        double keywordDensity = calculateKeywordDensity(content, targetKeyword);
        auditResults.put("keywordDensity", keywordDensity);
        
        // 2. Readability Score (Flesch-Kincaid approximation)
        double readabilityScore = calculateReadabilityScore(content);
        auditResults.put("readabilityScore", readabilityScore);
        
        // 3. Title Optimization Check
        boolean titleOptimized = analyzeTitleOptimization(title, targetKeyword);
        auditResults.put("titleOptimized", titleOptimized);
        
        // 4. Content Length Analysis
        int wordCount = countWords(content);
        auditResults.put("wordCount", wordCount);
        boolean lengthSufficient = wordCount > 300;
        
        // 5. Link Analysis (Internal vs External)
        Map<String, Integer> linkStats = analyzeLinks(content);
        auditResults.put("linkStatistics", linkStats);

        // Aggregate Scoring Logic (Complex weighting)
        double finalScore = 0.0;
        
        // Keyword density ideal range: 1% to 3%
        if (keywordDensity > 0.01 && keywordDensity < 0.03) {
            finalScore += 30; // Max points for perfect density
        } else if (keywordDensity > 0) {
            finalScore += 15; // Some points for having the keyword
        } else {
            finalScore -= 10; // Penalty for keyword stuffing or absence
        }

        // Readability: Higher is easier to read. Target 60-70 for general audience
        if (readabilityScore >= 60) {
            finalScore += 25;
        } else if (readabilityScore >= 40) {
            finalScore += 10;
        }

        if (titleOptimized) finalScore += 20;
        if (lengthSufficient) finalScore += 15;
        
        // Link scoring
        int internalLinks = linkStats.getOrDefault("internal", 0);
        int externalLinks = linkStats.getOrDefault("external", 0);
        if (internalLinks > 0) finalScore += 5;
        if (externalLinks > 0) finalScore += 5;

        // Cap at 100
        finalScore = Math.min(100.0, Math.max(0.0, finalScore));
        
        auditResults.put("aggregateScore", finalScore);
        
        // Generate actionable recommendations
        List<String> recommendations = generateRecommendations(
                keywordDensity, readabilityScore, titleOptimized, wordCount, internalLinks, externalLinks);
        auditResults.put("recommendations", recommendations);

        long executionTime = System.currentTimeMillis() - startTime;
        log.info("SEO Audit completed in {} ms. Final Score: {}", executionTime, finalScore);
        auditResults.put("auditExecutionTimeMs", executionTime);

        return auditResults;
    }

    private double calculateKeywordDensity(String content, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return 0.0;
        
        String lowerContent = content.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();
        
        int totalWords = countWords(lowerContent);
        if (totalWords == 0) return 0.0;

        int keywordCount = 0;
        int index = 0;
        while ((index = lowerContent.indexOf(lowerKeyword, index)) != -1) {
            keywordCount++;
            index += lowerKeyword.length();
        }
        
        // Assume keyword might be multi-word
        int wordsInKeyword = countWords(lowerKeyword);
        double adjustedKeywordCount = keywordCount * wordsInKeyword;

        return adjustedKeywordCount / (double) totalWords;
    }

    private double calculateReadabilityScore(String text) {
        // Highly simplified Flesch Reading Ease approximation for demonstration
        int totalWords = countWords(text);
        int totalSentences = countSentences(text);
        int totalSyllables = countSyllablesApproximation(text);

        if (totalWords == 0 || totalSentences == 0) return 0.0;

        double score = 206.835 - 1.015 * ((double) totalWords / totalSentences) - 84.6 * ((double) totalSyllables / totalWords);
        return Math.max(0, Math.min(100, score));
    }

    private boolean analyzeTitleOptimization(String title, String keyword) {
        if (title == null || keyword == null) return false;
        // Check if keyword is in title, and title is a good length (50-60 chars)
        boolean hasKeyword = title.toLowerCase().contains(keyword.toLowerCase());
        boolean goodLength = title.length() >= 40 && title.length() <= 65;
        return hasKeyword && goodLength;
    }

    private Map<String, Integer> analyzeLinks(String content) {
        int internalLinks = 0;
        int externalLinks = 0;
        
        Pattern linkPattern = Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1");
        Matcher matcher = linkPattern.matcher(content);
        
        while (matcher.find()) {
            String url = matcher.group(2);
            if (url.startsWith("/") || url.contains("merryblue.com")) {
                internalLinks++;
            } else if (url.startsWith("http")) {
                externalLinks++;
            }
        }
        return Map.of("internal", internalLinks, "external", externalLinks);
    }

    private List<String> generateRecommendations(double density, double readability, boolean titleOpt, int words, int internal, int external) {
        List<String> recommendations = new ArrayList<>();
        if (density < 0.01) recommendations.add("Increase the usage of your target keyword throughout the text.");
        if (density > 0.03) recommendations.add("Warning: Keyword density is very high. Avoid keyword stuffing.");
        if (readability < 50) recommendations.add("Consider simplifying your sentences to improve readability score.");
        if (!titleOpt) recommendations.add("Optimize your title: ensure it contains the keyword and is between 40-65 characters.");
        if (words < 300) recommendations.add("Content is too short. Search engines favor long-form content (>300 words).");
        if (internal == 0) recommendations.add("Add internal links to other relevant pages on your site.");
        if (external == 0) recommendations.add("Add outbound links to authoritative external sources.");
        return recommendations;
    }

    private int countWords(String text) {
        if (text == null || text.isEmpty()) return 0;
        String[] words = text.split("\\s+");
        return words.length;
    }

    private int countSentences(String text) {
        if (text == null || text.isEmpty()) return 0;
        String[] sentences = text.split("[.!?]");
        return sentences.length;
    }

    private int countSyllablesApproximation(String text) {
        // Very rough approximation: count vowels
        int count = 0;
        String lower = text.toLowerCase();
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y') {
                count++;
            }
        }
        // Assuming ~1.5 vowels per syllable roughly
        return (int) (count / 1.5);
    }
}
