package com.merryblue.api.service;

import com.merryblue.api.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialMediaSyncService {

    private final BlogPostRepository blogRepository;
    private final WebClient webClient;

    public void syncLatestPostsToFacebook(String accessToken) {
        log.info("Syncing latest blog posts to social media channels...");
        
        blogRepository.findByIsPublishedTrueOrderByDisplayOrderAsc().stream()
                .limit(5)
                .forEach(post -> {
                    // Complex Logic: Check if already synced, format message, call Graph API
                    log.info("Posting: {} to Facebook", post.getTitleVi());
                    // Mock API Call
                });
    }
}
