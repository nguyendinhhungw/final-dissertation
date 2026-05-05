package com.merryblue.api.service;

import com.merryblue.api.dto.BlogPostDTO;
import com.merryblue.api.mapper.BlogPostMapper;
import com.merryblue.api.model.BlogPost;
import com.merryblue.api.repository.BlogPostRepository;
import com.merryblue.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final BlogPostMapper blogPostMapper;

    public List<BlogPostDTO> getAllPosts(boolean onlyPublished) {
        List<BlogPost> posts;
        if (onlyPublished) {
            posts = blogPostRepository.findByIsPublishedTrueOrderByDisplayOrderAsc();
        } else {
            posts = blogPostRepository.findAllByOrderByDisplayOrderAsc();
        }
        return posts.stream().map(blogPostMapper::toDTO).collect(Collectors.toList());
    }

    public BlogPostDTO getPostBySlug(String slug) {
        return blogPostRepository.findBySlug(slug)
                .map(blogPostMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found"));
    }

    public BlogPost getPostEntityById(UUID id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found"));
    }

    @Transactional
    public BlogPostDTO incrementViews(String slug) {
        BlogPost post = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found"));
        post.setViews(post.getViews() + 1);
        return blogPostMapper.toDTO(blogPostRepository.save(post));
    }

    @Transactional
    public BlogPostDTO createPost(BlogPostDTO postDTO) {
        BlogPost post = blogPostMapper.toEntity(postDTO);
        return blogPostMapper.toDTO(blogPostRepository.save(post));
    }

    @Transactional
    public BlogPostDTO updatePost(UUID id, BlogPostDTO updatedDTO) {
        BlogPost existing = getPostEntityById(id);
        BlogPost updated = blogPostMapper.toEntity(updatedDTO);
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        return blogPostMapper.toDTO(blogPostRepository.save(updated));
    }

    @Transactional
    public void deletePost(UUID id) {
        blogPostRepository.deleteById(id);
    }
}
