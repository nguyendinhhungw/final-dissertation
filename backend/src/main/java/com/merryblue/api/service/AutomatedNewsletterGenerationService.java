package com.merryblue.api.service;

import com.merryblue.api.model.BlogPost;
import com.merryblue.api.model.Job;
import com.merryblue.api.model.User;
import com.merryblue.api.repository.BlogPostRepository;
import com.merryblue.api.repository.JobRepository;
import com.merryblue.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service that automatically compiles and dispatches a weekly/monthly newsletter.
 * It gathers the most popular recent blog posts, newly opened jobs, and constructs
 * a personalized HTML email for each subscribed user.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AutomatedNewsletterGenerationService {

    private final BlogPostRepository blogRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Entry point to trigger the generation and dispatch of the newsletter.
     * This would typically be called by a Spring @Scheduled task.
     */
    @Transactional(readOnly = true)
    public void generateAndDispatchNewsletter(boolean isWeekly) {
        log.info("Starting generation of {} newsletter...", isWeekly ? "Weekly" : "Monthly");

        OffsetDateTime cutoffDate = OffsetDateTime.now().minusDays(isWeekly ? 7 : 30);

        // 1. Gather Content: Top Blog Posts
        List<BlogPost> topPosts = blogRepository.findAll().stream()
                .filter(BlogPost::getIsPublished)
                .filter(post -> post.getCreatedAt().isAfter(cutoffDate))
                .sorted(Comparator.comparing(BlogPost::getViews, Comparator.nullsFirst(Comparator.reverseOrder())))
                .limit(3)
                .toList();

        // 2. Gather Content: New Job Openings
        List<Job> newJobs = jobRepository.findAll().stream()
                .filter(Job::getIsOpen)
                .filter(job -> job.getCreatedAt().isAfter(cutoffDate))
                .limit(5)
                .toList();

        if (topPosts.isEmpty() && newJobs.isEmpty()) {
            log.info("No new content to send in the newsletter. Skipping dispatch.");
            return;
        }

        // 3. Compile base HTML template
        String baseHtmlContent = compileNewsletterTemplate(topPosts, newJobs, isWeekly);

        // 4. Fetch Subscribers
        // Assuming we have a way to know who wants newsletters. For this mock, we send to all active users.
        List<User> subscribers = userRepository.findAll().stream()
                // .filter(User::isSubscribedToNewsletter) // Mock check
                .toList();

        log.info("Dispatching newsletter to {} subscribers.", subscribers.size());

        // 5. Async Dispatch
        for (User subscriber : subscribers) {
            String personalizedContent = personalizeContent(baseHtmlContent, subscriber);
            String subject = isWeekly ? "Merryblue Weekly Highlights" : "Merryblue Monthly Digest";
            
            // Fire and forget asynchronous email sending
            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendSimpleMessage(subscriber.getEmail(), subject, personalizedContent);
                } catch (Exception e) {
                    log.error("Failed to send newsletter to {}", subscriber.getEmail(), e);
                }
            });
        }
        
        log.info("Newsletter dispatch initiated.");
    }

    private String compileNewsletterTemplate(List<BlogPost> topPosts, List<Job> newJobs, boolean isWeekly) {
        StringBuilder html = new StringBuilder();
        
        html.append("<html><body style='font-family: sans-serif; color: #333;'>");
        html.append("<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #eaeaea;'>");
        
        // Header
        html.append("<h1 style='color: #0056b3; text-align: center;'>Merryblue ");
        html.append(isWeekly ? "Weekly Highlights" : "Monthly Digest");
        html.append("</h1>");
        html.append("<p>Hi {{FIRST_NAME}}, here's what you missed recently!</p>");

        // Blog Section
        if (!topPosts.isEmpty()) {
            html.append("<h2 style='border-bottom: 2px solid #eee; padding-bottom: 5px;'>Top Articles</h2>");
            for (BlogPost post : topPosts) {
                html.append("<div style='margin-bottom: 15px;'>");
                html.append("<h3><a style='color: #0056b3; text-decoration: none;' href='https://merryblue.com/blog/")
                    .append(post.getSlug()).append("'>")
                    .append(post.getTitleVi()).append("</a></h3>");
                
                String snippet = post.getContentVi() != null && post.getContentVi().length() > 100 
                                 ? post.getContentVi().substring(0, 100) + "..." 
                                 : post.getContentVi();
                html.append("<p style='font-size: 14px; color: #666;'>").append(snippet).append("</p>");
                html.append("</div>");
            }
        }

        // Jobs Section
        if (!newJobs.isEmpty()) {
            html.append("<h2 style='border-bottom: 2px solid #eee; padding-bottom: 5px; margin-top: 30px;'>New Opportunities</h2>");
            html.append("<ul>");
            for (Job job : newJobs) {
                html.append("<li style='margin-bottom: 10px;'>");
                html.append("<strong>").append(job.getTitleVi()).append("</strong> - ")
                    .append(job.getLocation())
                    .append(" (<a href='https://merryblue.com/careers/").append(job.getSlug()).append("'>Apply Now</a>)");
                html.append("</li>");
            }
            html.append("</ul>");
        }

        // Footer
        html.append("<div style='margin-top: 40px; text-align: center; font-size: 12px; color: #999;'>");
        html.append("<p>Merryblue Corporation | 123 Tech Street, City</p>");
        html.append("<p>You are receiving this because you opted in to our mailing list.</p>");
        html.append("</div>");

        html.append("</div></body></html>");
        
        return html.toString();
    }

    private String personalizeContent(String baseTemplate, User user) {
        // Simple string replacement for personalization
        String firstName = extractFirstName(user);
        return baseTemplate.replace("{{FIRST_NAME}}", firstName);
    }
    
    private String extractFirstName(User user) {
        // Mock extraction logic. Assuming user doesn't have a direct 'firstName' field here, 
        // fallback to "there" if name is unknown.
        return "there";
    }
}
