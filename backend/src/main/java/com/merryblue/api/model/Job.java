package com.merryblue.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "title_vi", nullable = false)
    private String titleVi;

    @Column(name = "title_en", nullable = false)
    private String titleEn;

    private String department;
    private String location;

    @Column(name = "employment_type")
    private String employmentType;

    @Column(name = "salary_range")
    private String salaryRange;

    @Column(name = "short_vi")
    private String shortVi;

    @Column(name = "short_en")
    private String shortEn;

    @Column(name = "description_vi")
    private String descriptionVi;

    @Column(name = "description_en")
    private String descriptionEn;

    @Column(name = "requirements_vi")
    private String requirementsVi;

    @Column(name = "requirements_en")
    private String requirementsEn;

    @Column(name = "benefits_vi")
    private String benefitsVi;

    @Column(name = "benefits_en")
    private String benefitsEn;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = true;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
