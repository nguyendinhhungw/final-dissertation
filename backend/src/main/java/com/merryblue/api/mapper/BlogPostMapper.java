package com.merryblue.api.mapper;

import com.merryblue.api.dto.BlogPostDTO;
import com.merryblue.api.model.BlogPost;
import org.springframework.stereotype.Component;

@Component
public class BlogPostMapper {

    public BlogPostDTO toDTO(BlogPost post) {
        if (post == null) return null;
        BlogPostDTO dto = new BlogPostDTO();
        dto.setId(post.getId());
        dto.setSlug(post.getSlug());
        dto.setTitleVi(post.getTitleVi());
        dto.setTitleEn(post.getTitleEn());
        dto.setExcerptVi(post.getExcerptVi());
        dto.setExcerptEn(post.getExcerptEn());
        dto.setBodyVi(post.getBodyVi());
        dto.setBodyEn(post.getBodyEn());
        dto.setCoverUrl(post.getCoverUrl());
        dto.setCategory(post.getCategory());
        dto.setTags(post.getTags());
        dto.setAuthor(post.getAuthor());
        dto.setIsPublished(post.getIsPublished());
        dto.setIsFeatured(post.getIsFeatured());
        dto.setViews(post.getViews());
        dto.setDisplayOrder(post.getDisplayOrder());
        dto.setPublishedAt(post.getPublishedAt());
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }

    public BlogPost toEntity(BlogPostDTO dto) {
        if (dto == null) return null;
        BlogPost post = new BlogPost();
        post.setId(dto.getId());
        post.setSlug(dto.getSlug());
        post.setTitleVi(dto.getTitleVi());
        post.setTitleEn(dto.getTitleEn());
        post.setExcerptVi(dto.getExcerptVi());
        post.setExcerptEn(dto.getExcerptEn());
        post.setBodyVi(dto.getBodyVi());
        post.setBodyEn(dto.getBodyEn());
        post.setCoverUrl(dto.getCoverUrl());
        post.setCategory(dto.getCategory());
        post.setTags(dto.getTags());
        post.setAuthor(dto.getAuthor());
        post.setIsPublished(dto.getIsPublished());
        post.setIsFeatured(dto.getIsFeatured());
        post.setViews(dto.getViews());
        post.setDisplayOrder(dto.getDisplayOrder());
        post.setPublishedAt(dto.getPublishedAt());
        post.setCreatedAt(dto.getCreatedAt());
        return post;
    }
}
