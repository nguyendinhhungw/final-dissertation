package com.merryblue.api.service;

import com.merryblue.api.dto.DashboardDTO;
import com.merryblue.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PortfolioProjectRepository portfolioRepository;
    private final BlogPostRepository blogRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final ContactRepository contactRepository;

    public DashboardDTO getStats() {
        return DashboardDTO.builder()
                .totalProjects(portfolioRepository.count())
                .totalBlogPosts(blogRepository.count())
                .totalJobs(jobRepository.count())
                .totalApplications(applicationRepository.count())
                .unreadContacts(contactRepository.countByIsReadFalse())
                .totalViews(blogRepository.findAll().stream().mapToLong(b -> b.getViews() != null ? b.getViews() : 0).sum())
                .build();
    }
}
