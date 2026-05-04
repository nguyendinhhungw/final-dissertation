package com.merryblue.api.repository;

import com.merryblue.api.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, UUID> {
    Optional<BlogPost> findBySlug(String slug);
    List<BlogPost> findByIsPublishedTrueOrderByDisplayOrderAsc();
    List<BlogPost> findAllByOrderByDisplayOrderAsc();
}
