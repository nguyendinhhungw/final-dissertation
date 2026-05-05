package com.merryblue.api.service;

import com.merryblue.api.dto.BlogPostDTO;
import com.merryblue.api.dto.JobDTO;
import com.merryblue.api.dto.PortfolioProjectDTO;
import com.merryblue.api.mapper.BlogPostMapper;
import com.merryblue.api.mapper.JobMapper;
import com.merryblue.api.mapper.PortfolioMapper;
import com.merryblue.api.repository.BlogPostRepository;
import com.merryblue.api.repository.JobRepository;
import com.merryblue.api.repository.PortfolioProjectRepository;
import com.merryblue.api.repository.specification.BlogPostSpecification;
import com.merryblue.api.repository.specification.JobSpecification;
import com.merryblue.api.repository.specification.PortfolioSpecification;
import com.merryblue.api.repository.specification.SearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final BlogPostRepository blogRepository;
    private final JobRepository jobRepository;
    private final PortfolioProjectRepository portfolioRepository;
    
    private final BlogPostMapper blogMapper;
    private final JobMapper jobMapper;
    private final PortfolioMapper portfolioMapper;

    public List<BlogPostDTO> searchBlog(String query) {
        SearchCriteria criteria = new SearchCriteria("titleVi", ":", query);
        BlogPostSpecification spec = new BlogPostSpecification(criteria);
        return blogRepository.findAll(spec).stream().map(blogMapper::toDTO).collect(Collectors.toList());
    }

    public List<JobDTO> searchJobs(String query) {
        SearchCriteria criteria = new SearchCriteria("titleVi", ":", query);
        JobSpecification spec = new JobSpecification(criteria);
        return jobRepository.findAll(spec).stream().map(jobMapper::toDTO).collect(Collectors.toList());
    }

    public List<PortfolioProjectDTO> searchPortfolio(String query) {
        SearchCriteria criteria = new SearchCriteria("titleVi", ":", query);
        PortfolioSpecification spec = new PortfolioSpecification(criteria);
        return portfolioRepository.findAll(spec).stream().map(portfolioMapper::toDTO).collect(Collectors.toList());
    }
}
