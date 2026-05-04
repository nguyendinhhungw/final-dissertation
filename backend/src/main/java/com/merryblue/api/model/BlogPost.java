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
@Table(name = "blog_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "title_vi", nullable = false)
    private String titleVi;

    @Column(name = "title_en", nullable = false)
    private String titleEn;

    @Column(name = "excerpt_vi")
    private String excerptVi;

    @Column(name = "excerpt_en")
    private String excerptEn;

    @Column(name = "body_vi")
    private String bodyVi;

    @Column(name = "body_en")
    private String bodyEn;

    @Column(name = "cover_url")
    private String coverUrl;

    private String category;

    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> tags;

    private String author = "Merryblue";

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(nullable = false)
    private Integer views = 0;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "published_at", nullable = false)
    private OffsetDateTime publishedAt = OffsetDateTime.now();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
