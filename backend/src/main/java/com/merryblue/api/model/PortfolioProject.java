package com.merryblue.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "portfolio_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioProject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "title_vi", nullable = false)
    private String titleVi;

    @Column(name = "title_en", nullable = false)
    private String titleEn;

    @Column(name = "short_vi")
    private String shortVi;

    @Column(name = "short_en")
    private String shortEn;

    @Column(name = "body_vi")
    private String bodyVi;

    @Column(name = "body_en")
    private String bodyEn;

    @Column(name = "cover_url")
    private String coverUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    private String gallery; // jsonb

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tech_stack")
    private List<String> techStack;

    private String category;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
