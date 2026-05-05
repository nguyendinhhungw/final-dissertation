package com.merryblue.api.controller;

import com.merryblue.api.dto.*;
import com.merryblue.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicController {

    private final BusinessServiceService businessServiceService;
    private final PortfolioService portfolioService;
    private final BlogPostService blogPostService;
    private final JobService jobService;
    private final SiteContentService siteContentService;

    @GetMapping("/services")
    public List<ServiceDTO> getServices() {
        return businessServiceService.getAllServices(true);
    }

    @GetMapping("/services/{slug}")
    public ServiceDTO getService(@PathVariable String slug) {
        return businessServiceService.getServiceBySlug(slug);
    }

    @GetMapping("/portfolio")
    public List<PortfolioProjectDTO> getPortfolio() {
        return portfolioService.getAllProjects(true);
    }

    @GetMapping("/portfolio/{slug}")
    public PortfolioProjectDTO getProject(@PathVariable String slug) {
        return portfolioService.getProjectBySlug(slug);
    }

    @GetMapping("/blog")
    public List<BlogPostDTO> getBlogPosts() {
        return blogPostService.getAllPosts(true);
    }

    @GetMapping("/blog/{slug}")
    public BlogPostDTO getBlogPost(@PathVariable String slug) {
        return blogPostService.incrementViews(slug);
    }

    @GetMapping("/jobs")
    public List<JobDTO> getJobs() {
        return jobService.getAllJobs(true);
    }

    @GetMapping("/jobs/{slug}")
    public JobDTO getJob(@PathVariable String slug) {
        return jobService.getJobBySlug(slug);
    }

    @GetMapping("/site-content")
    public List<SiteContentDTO> getAllSiteContent() {
        return siteContentService.getAllContent();
    }

    @GetMapping("/site-content/{key}")
    public SiteContentDTO getSiteContent(@PathVariable String key) {
        return siteContentService.getContentByKey(key);
    }

    @PostMapping("/contacts")
    public ContactDTO sendContact(@jakarta.validation.Valid @RequestBody ContactDTO contactDTO) {
        return contactService.createContact(contactDTO);
    }
}
