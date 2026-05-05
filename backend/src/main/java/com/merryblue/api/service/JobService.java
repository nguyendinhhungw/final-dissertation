package com.merryblue.api.service;

import com.merryblue.api.dto.JobDTO;
import com.merryblue.api.mapper.JobMapper;
import com.merryblue.api.model.Job;
import com.merryblue.api.repository.JobRepository;
import com.merryblue.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    public List<JobDTO> getAllJobs(boolean onlyOpen) {
        List<Job> jobs;
        if (onlyOpen) {
            jobs = jobRepository.findByIsOpenTrueOrderByDisplayOrderAsc();
        } else {
            jobs = jobRepository.findAllByOrderByDisplayOrderAsc();
        }
        return jobs.stream().map(jobMapper::toDTO).collect(Collectors.toList());
    }

    public JobDTO getJobBySlug(String slug) {
        return jobRepository.findBySlug(slug)
                .map(jobMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }

    public Job getJobEntityById(UUID id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }

    @Transactional
    public JobDTO createJob(JobDTO jobDTO) {
        Job job = jobMapper.toEntity(jobDTO);
        return jobMapper.toDTO(jobRepository.save(job));
    }

    @Transactional
    public JobDTO updateJob(UUID id, JobDTO updatedDTO) {
        Job existing = getJobEntityById(id);
        Job updated = jobMapper.toEntity(updatedDTO);
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        return jobMapper.toDTO(jobRepository.save(updated));
    }

    @Transactional
    public void deleteJob(UUID id) {
        jobRepository.deleteById(id);
    }
}
