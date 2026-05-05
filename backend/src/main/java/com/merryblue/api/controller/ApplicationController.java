package com.merryblue.api.controller;

import com.merryblue.api.dto.JobApplicationDTO;
import com.merryblue.api.model.JobApplication;
import com.merryblue.api.model.Job;
import com.merryblue.api.service.ApplicationService;
import com.merryblue.api.service.JobService;
import com.merryblue.api.security.MerryblueUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JobService jobService;

    @GetMapping("/me/applications")
    public List<JobApplicationDTO> getMyApplications(@AuthenticationPrincipal MerryblueUserPrincipal principal) {
        UUID userId = UUID.fromString(principal.getId());
        return applicationService.getApplicationsByUserId(userId);
    }

    @PostMapping("/applications")
    public JobApplicationDTO submitApplication(
            @AuthenticationPrincipal MerryblueUserPrincipal principal,
            @RequestBody JobApplication applicationRequest) {
        
        UUID userId = UUID.fromString(principal.getId());
        
        Job job = jobService.getJobEntityById(applicationRequest.getJob().getId());

        JobApplication app = new JobApplication();
        app.setUserId(userId);
        app.setJob(job);
        app.setFullName(applicationRequest.getFullName());
        app.setEmail(applicationRequest.getEmail());
        app.setPhone(applicationRequest.getPhone());
        app.setCvPath(applicationRequest.getCvPath());
        app.setCoverLetter(applicationRequest.getCoverLetter());
        app.setStatus("submitted");
        
        return applicationService.createApplication(app);
    }
}
