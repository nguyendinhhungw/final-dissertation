package com.merryblue.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDTO {
    private long totalProjects;
    private long totalBlogPosts;
    private long totalJobs;
    private long totalApplications;
    private long unreadContacts;
    private long totalViews;
}
