package com.merryblue.api.controller;

import com.merryblue.api.dto.*;
import com.merryblue.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BusinessServiceService businessServiceService;
    private final PortfolioService portfolioService;
    private final BlogPostService blogPostService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final ContactService contactService;

    // --- SERVICES ---
    @GetMapping("/services")
    public List<ServiceDTO> getAllServices() {
        return businessServiceService.getAllServices(false);
    }

    @PostMapping("/services")
    public ServiceDTO createService(@RequestBody ServiceDTO service) {
        return businessServiceService.createService(service);
    }

    @PutMapping("/services/{id}")
    public ServiceDTO updateService(@PathVariable UUID id, @RequestBody ServiceDTO updated) {
        return businessServiceService.updateService(id, updated);
    }

    @DeleteMapping("/services/{id}")
    public void deleteService(@PathVariable UUID id) {
        businessServiceService.deleteService(id);
    }

    // --- PORTFOLIO ---
    @GetMapping("/portfolio")
    public List<PortfolioProjectDTO> getAllPortfolio() {
        return portfolioService.getAllProjects(false);
    }

    @PostMapping("/portfolio")
    public PortfolioProjectDTO createPortfolio(@RequestBody PortfolioProjectDTO project) {
        return portfolioService.createProject(project);
    }

    @PutMapping("/portfolio/{id}")
    public PortfolioProjectDTO updatePortfolio(@PathVariable UUID id, @RequestBody PortfolioProjectDTO updated) {
        return portfolioService.updateProject(id, updated);
    }

    @DeleteMapping("/portfolio/{id}")
    public void deletePortfolio(@PathVariable UUID id) {
        portfolioService.deleteProject(id);
    }

    // --- BLOG ---
    @GetMapping("/blog")
    public List<BlogPostDTO> getAllBlogPosts() {
        return blogPostService.getAllPosts(false);
    }

    @PostMapping("/blog")
    public BlogPostDTO createBlogPost(@RequestBody BlogPostDTO post) {
        return blogPostService.createPost(post);
    }

    @PutMapping("/blog/{id}")
    public BlogPostDTO updateBlogPost(@PathVariable UUID id, @RequestBody BlogPostDTO updated) {
        return blogPostService.updatePost(id, updated);
    }

    @DeleteMapping("/blog/{id}")
    public void deleteBlogPost(@PathVariable UUID id) {
        blogPostService.deletePost(id);
    }

    // --- JOBS ---
    @GetMapping("/jobs")
    public List<JobDTO> getAllJobs() {
        return jobService.getAllJobs(false);
    }

    @PostMapping("/jobs")
    public JobDTO createJob(@RequestBody JobDTO job) {
        return jobService.createJob(job);
    }

    @PutMapping("/jobs/{id}")
    public JobDTO updateJob(@PathVariable UUID id, @RequestBody JobDTO updated) {
        return jobService.updateJob(id, updated);
    }

    @DeleteMapping("/jobs/{id}")
    public void deleteJob(@PathVariable UUID id) {
        jobService.deleteJob(id);
    }

    // --- APPLICATIONS ---
    @GetMapping("/applications")
    public List<JobApplicationDTO> getAllApplications() {
        return applicationService.getAllApplications();
    }

    @PutMapping("/applications/{id}")
    public JobApplicationDTO updateApplicationStatus(@PathVariable UUID id, @RequestBody JobApplicationDTO updated) {
        return applicationService.updateStatus(id, updated.getStatus(), updated.getAdminNotes());
    }

    @DeleteMapping("/applications/{id}")
    public void deleteApplication(@PathVariable UUID id) {
        applicationService.deleteApplication(id);
    }

    // --- CONTACTS ---
    @GetMapping("/contacts")
    public List<ContactDTO> getAllContacts() {
        return contactService.getAllContacts();
    }

    @PutMapping("/contacts/{id}/read")
    public ContactDTO markContactRead(@PathVariable UUID id) {
        return contactService.markAsRead(id);
    }

    @DeleteMapping("/contacts/{id}")
    public void deleteContact(@PathVariable UUID id) {
        contactService.deleteContact(id);
    }
}
