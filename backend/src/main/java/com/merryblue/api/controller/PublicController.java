package com.merryblue.api.controller;

import com.merryblue.api.model.*;
import com.merryblue.api.repository.*;
import com.merryblue.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicController {

    private final ServiceRepository serviceRepository;
    private final PortfolioProjectRepository portfolioProjectRepository;
    private final BlogPostRepository blogPostRepository;
    private final JobRepository jobRepository;
    private final SiteContentRepository siteContentRepository;

    @GetMapping("/services")
    public List<Service> getServices() {
        return serviceRepository.findByIsPublishedTrueOrderByDisplayOrderAsc();
    }

    @GetMapping("/services/{slug}")
    public Service getService(@PathVariable String slug) {
        return serviceRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
    }

    @GetMapping("/portfolio")
    public List<PortfolioProject> getPortfolio() {
        return portfolioProjectRepository.findByIsPublishedTrueOrderByDisplayOrderAsc();
    }

    @GetMapping("/portfolio/{slug}")
    public PortfolioProject getProject(@PathVariable String slug) {
        return portfolioProjectRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    @GetMapping("/blog")
    public List<BlogPost> getBlogPosts() {
        return blogPostRepository.findByIsPublishedTrueOrderByDisplayOrderAsc();
    }

    @GetMapping("/blog/{slug}")
    public BlogPost getBlogPost(@PathVariable String slug) {
        BlogPost post = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found"));
        post.setViews(post.getViews() + 1);
        return blogPostRepository.save(post);
    }

    @GetMapping("/jobs")
    public List<Job> getJobs() {
        return jobRepository.findByIsOpenTrueOrderByDisplayOrderAsc();
    }

    @GetMapping("/jobs/{slug}")
    public Job getJob(@PathVariable String slug) {
        return jobRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }

    @GetMapping("/site-content")
    public List<SiteContent> getAllSiteContent() {
        return siteContentRepository.findAll();
    }

    @GetMapping("/site-content/{key}")
    public SiteContent getSiteContent(@PathVariable String key) {
        return siteContentRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));
    }
}
