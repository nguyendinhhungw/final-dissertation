package com.merryblue.api.service;

import com.merryblue.api.dto.JobApplicationDTO;
import com.merryblue.api.mapper.JobApplicationMapper;
import com.merryblue.api.model.JobApplication;
import com.merryblue.api.repository.JobApplicationRepository;
import com.merryblue.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobApplicationMapper applicationMapper;

    public List<JobApplicationDTO> getAllApplications() {
        return applicationRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(applicationMapper::toDTO).collect(Collectors.toList());
    }

    public List<JobApplicationDTO> getApplicationsByUserId(UUID userId) {
        return applicationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(applicationMapper::toDTO).collect(Collectors.toList());
    }

    public JobApplication getApplicationEntityById(UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    @Transactional
    public JobApplicationDTO createApplication(JobApplication application) {
        return applicationMapper.toDTO(applicationRepository.save(application));
    }

    @Transactional
    public JobApplicationDTO updateStatus(UUID id, String status, String adminNotes) {
        JobApplication application = getApplicationEntityById(id);
        if (status != null) application.setStatus(status);
        if (adminNotes != null) application.setAdminNotes(adminNotes);
        return applicationMapper.toDTO(applicationRepository.save(application));
    }

    @Transactional
    public void deleteApplication(UUID id) {
        applicationRepository.deleteById(id);
    }
}
