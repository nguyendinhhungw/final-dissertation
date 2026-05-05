package com.merryblue.api.controller;

import com.merryblue.api.dto.ApiResponse;
import com.merryblue.api.dto.BlogPostDTO;
import com.merryblue.api.dto.JobDTO;
import com.merryblue.api.dto.PortfolioProjectDTO;
import com.merryblue.api.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/blog")
    public ApiResponse<List<BlogPostDTO>> searchBlog(@RequestParam String q) {
        return ApiResponse.success(searchService.searchBlog(q));
    }

    @GetMapping("/jobs")
    public ApiResponse<List<JobDTO>> searchJobs(@RequestParam String q) {
        return ApiResponse.success(searchService.searchJobs(q));
    }

    @GetMapping("/portfolio")
    public ApiResponse<List<PortfolioProjectDTO>> searchPortfolio(@RequestParam String q) {
        return ApiResponse.success(searchService.searchPortfolio(q));
    }
}
