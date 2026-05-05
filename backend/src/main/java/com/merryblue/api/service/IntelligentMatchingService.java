package com.merryblue.api.service;

import com.merryblue.api.model.Job;
import com.merryblue.api.model.JobApplication;
import com.merryblue.api.repository.JobApplicationRepository;
import com.merryblue.api.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for intelligently matching job applications to specific job requirements
 * using a multi-factor scoring algorithm. Analyzes skills, experience, and textual descriptions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IntelligentMatchingService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    // Simulated weights for different matching factors
    private static final double WEIGHT_SKILLS_MATCH = 0.50;
    private static final double WEIGHT_EXPERIENCE_LEVEL = 0.30;
    private static final double WEIGHT_TEXT_SIMILARITY = 0.20;

    /**
     * Calculates a matching score (0-100) for a given application against its target job.
     */
    public Map<String, Object> evaluateApplicationMatch(UUID applicationId) {
        log.info("Starting intelligent matching evaluation for application ID: {}", applicationId);
        
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
                
        Job job = application.getJob();
        if (job == null) {
            return Map.of("error", "Application is not associated with a specific job", "score", 0);
        }

        Map<String, Object> evaluationDetails = new HashMap<>();
        
        // 1. Extract requirements from Job (Mocking NLP extraction)
        Set<String> jobRequiredSkills = extractSkillsFromText(job.getDescriptionEn() + " " + job.getDescriptionVi());
        evaluationDetails.put("jobRequiredSkills", jobRequiredSkills);
        
        // 2. Extract applicant skills from Cover Letter/CV text (Mocking parsing)
        Set<String> applicantSkills = extractSkillsFromText(application.getCoverLetter());
        evaluationDetails.put("applicantSkills", applicantSkills);

        // 3. Calculate Skills Match Score
        double skillsScore = calculateSkillsOverlap(jobRequiredSkills, applicantSkills);
        evaluationDetails.put("skillsScore", skillsScore);

        // 4. Calculate Experience Match (Mocking logic based on text patterns)
        double experienceScore = estimateExperienceMatch(job.getDescriptionEn(), application.getCoverLetter());
        evaluationDetails.put("experienceScore", experienceScore);

        // 5. Calculate Text Similarity (Cosine similarity approximation)
        double textSimilarityScore = calculateTextSimilarity(
                job.getDescriptionEn(), application.getCoverLetter());
        evaluationDetails.put("textSimilarityScore", textSimilarityScore);

        // 6. Aggregate Final Score
        double finalScore = (skillsScore * WEIGHT_SKILLS_MATCH) +
                            (experienceScore * WEIGHT_EXPERIENCE_LEVEL) +
                            (textSimilarityScore * WEIGHT_TEXT_SIMILARITY);

        // Normalize to 100
        finalScore = Math.min(100.0, Math.max(0.0, finalScore));
        
        evaluationDetails.put("finalMatchScore", finalScore);
        
        // 7. Generate actionable insights for HR
        evaluationDetails.put("hrInsights", generateHrInsights(skillsScore, experienceScore, finalScore));

        log.info("Evaluation complete. Final Match Score: {}", finalScore);
        return evaluationDetails;
    }

    /**
     * Finds the top N best matching candidates for a specific job.
     */
    public List<Map<String, Object>> findTopMatchesForJob(UUID jobId, int limit) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        List<JobApplication> allApplications = jobApplicationRepository.findAll().stream()
                .filter(app -> app.getJob() != null && app.getJob().getId().equals(jobId))
                .toList();

        List<Map<String, Object>> rankedCandidates = new ArrayList<>();
        
        for (JobApplication app : allApplications) {
            Map<String, Object> eval = evaluateApplicationMatch(app.getId());
            eval.put("applicationId", app.getId());
            eval.put("applicantName", app.getFullName());
            rankedCandidates.add(eval);
        }

        // Sort by finalMatchScore descending
        rankedCandidates.sort((a, b) -> {
            Double scoreA = (Double) a.get("finalMatchScore");
            Double scoreB = (Double) b.get("finalMatchScore");
            return Double.compare(scoreB, scoreA);
        });

        return rankedCandidates.stream().limit(limit).collect(Collectors.toList());
    }

    private Set<String> extractSkillsFromText(String text) {
        if (text == null || text.trim().isEmpty()) return Collections.emptySet();
        
        // Mock list of known technical and soft skills
        Set<String> knownDictionary = Set.of(
            "java", "spring", "react", "node", "typescript", "sql", "aws", "docker",
            "communication", "leadership", "agile", "scrum", "python", "css", "html"
        );
        
        String lowerText = text.toLowerCase();
        return knownDictionary.stream()
                .filter(lowerText::contains)
                .collect(Collectors.toSet());
    }

    private double calculateSkillsOverlap(Set<String> required, Set<String> applicant) {
        if (required.isEmpty()) return 100.0; // If no specific skills required, assume match
        
        long matchCount = required.stream().filter(applicant::contains).count();
        return ((double) matchCount / required.size()) * 100.0;
    }

    private double estimateExperienceMatch(String jobDesc, String applicantDesc) {
        // Very rudimentary mock logic: look for years of experience mentions
        if (jobDesc == null || applicantDesc == null) return 50.0; // Neutral score
        
        int reqYears = extractYearsMention(jobDesc);
        int appYears = extractYearsMention(applicantDesc);
        
        if (reqYears == 0) return 80.0; // No strict requirement found
        if (appYears >= reqYears) return 100.0;
        if (appYears >= reqYears - 1) return 70.0; // Close enough
        return 30.0; // Significant gap
    }

    private int extractYearsMention(String text) {
        // Mock regex extraction for "\d+ years"
        String lower = text.toLowerCase();
        if (lower.contains("5 years") || lower.contains("5+ years")) return 5;
        if (lower.contains("3 years") || lower.contains("3+ years")) return 3;
        if (lower.contains("1 year") || lower.contains("1+ years")) return 1;
        return 0;
    }

    private double calculateTextSimilarity(String text1, String text2) {
        // Mock Jaccard similarity between raw words
        if (text1 == null || text2 == null) return 0.0;
        
        Set<String> words1 = Set.of(text1.toLowerCase().split("\\W+"));
        Set<String> words2 = Set.of(text2.toLowerCase().split("\\W+"));
        
        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);
        
        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);
        
        if (union.isEmpty()) return 0.0;
        return ((double) intersection.size() / union.size()) * 100.0;
    }

    private List<String> generateHrInsights(double skills, double exp, double total) {
        List<String> insights = new ArrayList<>();
        if (total > 85) insights.add("Highly Recommended Candidate. Fast-track for interview.");
        else if (total > 60) insights.add("Potential Candidate. Review cover letter carefully.");
        else insights.add("Low Match. May not meet core requirements.");
        
        if (skills < 50) insights.add("Warning: Significant skill gap detected.");
        if (exp < 50) insights.add("Warning: Experience level may be lower than requested.");
        
        return insights;
    }
}
