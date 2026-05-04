package com.merryblue.api.controller;

import com.merryblue.api.model.JobApplication;
import com.merryblue.api.model.Job;
import com.merryblue.api.repository.JobApplicationRepository;
import com.merryblue.api.repository.JobRepository;
import com.merryblue.api.security.MerryblueUserPrincipal;
import com.merryblue.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicationController {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    @GetMapping("/me/applications")
    public List<JobApplication> getMyApplications(@AuthenticationPrincipal MerryblueUserPrincipal principal) {
        UUID userId = UUID.fromString(principal.getId());
        return applicationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PostMapping("/applications")
    public JobApplication submitApplication(
            @AuthenticationPrincipal MerryblueUserPrincipal principal,
            @RequestBody JobApplication applicationRequest) {
        
        UUID userId = UUID.fromString(principal.getId());
        
        Job job = jobRepository.findById(applicationRequest.getJob().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        JobApplication app = new JobApplication();
        app.setUserId(userId);
        app.setJob(job);
        app.setFullName(applicationRequest.getFullName());
        app.setEmail(applicationRequest.getEmail());
        app.setPhone(applicationRequest.getPhone());
        app.setCvPath(applicationRequest.getCvPath());
        app.setCoverLetter(applicationRequest.getCoverLetter());
        app.setStatus("submitted");
        
        return applicationRepository.save(app);
    }
}
