package com.merryblue.api.controller;

import com.merryblue.api.model.*;
import com.merryblue.api.repository.*;
import com.merryblue.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ServiceRepository serviceRepository;
    private final PortfolioProjectRepository portfolioProjectRepository;
    private final BlogPostRepository blogPostRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final ContactRepository contactRepository;

    // --- SERVICES ---
    @GetMapping("/services")
    public List<Service> getAllServices() {
        return serviceRepository.findAllByOrderByDisplayOrderAsc();
    }

    @PostMapping("/services")
    public Service createService(@RequestBody Service service) {
        return serviceRepository.save(service);
    }

    @PutMapping("/services/{id}")
    public Service updateService(@PathVariable UUID id, @RequestBody Service updated) {
        Service existing = serviceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        return serviceRepository.save(updated);
    }

    @DeleteMapping("/services/{id}")
    public void deleteService(@PathVariable UUID id) {
        serviceRepository.deleteById(id);
    }

    // --- PORTFOLIO ---
    @GetMapping("/portfolio")
    public List<PortfolioProject> getAllPortfolio() {
        return portfolioProjectRepository.findAllByOrderByDisplayOrderAsc();
    }

    @PostMapping("/portfolio")
    public PortfolioProject createPortfolio(@RequestBody PortfolioProject project) {
        return portfolioProjectRepository.save(project);
    }

    @PutMapping("/portfolio/{id}")
    public PortfolioProject updatePortfolio(@PathVariable UUID id, @RequestBody PortfolioProject updated) {
        PortfolioProject existing = portfolioProjectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        return portfolioProjectRepository.save(updated);
    }

    @DeleteMapping("/portfolio/{id}")
    public void deletePortfolio(@PathVariable UUID id) {
        portfolioProjectRepository.deleteById(id);
    }

    // --- BLOG ---
    @GetMapping("/blog")
    public List<BlogPost> getAllBlogPosts() {
        return blogPostRepository.findAllByOrderByDisplayOrderAsc();
    }

    @PostMapping("/blog")
    public BlogPost createBlogPost(@RequestBody BlogPost post) {
        return blogPostRepository.save(post);
    }

    @PutMapping("/blog/{id}")
    public BlogPost updateBlogPost(@PathVariable UUID id, @RequestBody BlogPost updated) {
        BlogPost existing = blogPostRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        return blogPostRepository.save(updated);
    }

    @DeleteMapping("/blog/{id}")
    public void deleteBlogPost(@PathVariable UUID id) {
        blogPostRepository.deleteById(id);
    }

    // --- JOBS ---
    @GetMapping("/jobs")
    public List<Job> getAllJobs() {
        return jobRepository.findAllByOrderByDisplayOrderAsc();
    }

    @PostMapping("/jobs")
    public Job createJob(@RequestBody Job job) {
        return jobRepository.save(job);
    }

    @PutMapping("/jobs/{id}")
    public Job updateJob(@PathVariable UUID id, @RequestBody Job updated) {
        Job existing = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        return jobRepository.save(updated);
    }

    @DeleteMapping("/jobs/{id}")
    public void deleteJob(@PathVariable UUID id) {
        jobRepository.deleteById(id);
    }

    // --- APPLICATIONS ---
    @GetMapping("/applications")
    public List<JobApplication> getAllApplications() {
        return applicationRepository.findAllByOrderByCreatedAtDesc();
    }

    @PutMapping("/applications/{id}")
    public JobApplication updateApplicationStatus(@PathVariable UUID id, @RequestBody JobApplication updated) {
        JobApplication existing = applicationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        existing.setStatus(updated.getStatus());
        existing.setAdminNotes(updated.getAdminNotes());
        return applicationRepository.save(existing);
    }

    @DeleteMapping("/applications/{id}")
    public void deleteApplication(@PathVariable UUID id) {
        applicationRepository.deleteById(id);
    }

    // --- CONTACTS ---
    @GetMapping("/contacts")
    public List<Contact> getAllContacts() {
        return contactRepository.findAllByOrderByCreatedAtDesc();
    }

    @PutMapping("/contacts/{id}/read")
    public Contact markContactRead(@PathVariable UUID id) {
        Contact existing = contactRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        existing.setIsRead(true);
        return contactRepository.save(existing);
    }

    @DeleteMapping("/contacts/{id}")
    public void deleteContact(@PathVariable UUID id) {
        contactRepository.deleteById(id);
    }
}
