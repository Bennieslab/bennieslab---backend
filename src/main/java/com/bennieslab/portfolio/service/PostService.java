package com.bennieslab.portfolio.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bennieslab.portfolio.model.Post;
import com.bennieslab.portfolio.repository.PostRepository;
import com.bennieslab.portfolio.dto.PostDto;
import com.bennieslab.portfolio.dto.SkillDto;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public PostService(PostRepository postRepository, FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
    }

    public Optional<PostDto> getPostById(Long id) {
        return postRepository.findById(id)
                .map(this::convertToDtoWithPresignedUrl);
    }

    public List<PostDto> getPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());
    }

    public PostDto createPost(Post post) {
        Post savedPost = postRepository.save(post);
        return convertToDtoWithPresignedUrl(savedPost);
    }

    public PostDto updatePost(Long id, Post updatedPost) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setTitle(updatedPost.getTitle());
                    post.setContent(updatedPost.getContent());
                    post.setCategory(updatedPost.getCategory());
                    
                    if (updatedPost.getThumbnailUrl() != null) {
                        post.setThumbnailUrl(updatedPost.getThumbnailUrl());
                    }
                    
                    // Sync attached skills relationship
                    post.setSkills(updatedPost.getSkills());
                    
                    post.setLastUpdated(LocalDateTime.now());
                    return convertToDtoWithPresignedUrl(postRepository.save(post));
                })
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    private PostDto convertToDtoWithPresignedUrl(Post post) {
        String presignedUrl = null;
        if (post.getThumbnailUrl() != null && !post.getThumbnailUrl().isEmpty()) {
            presignedUrl = fileStorageService.getPresignedUrl(post.getThumbnailUrl());
        }

        // Safely extract flat skill metrics for blog highlights
        Set<SkillDto> skillDtos = post.getSkills() != null ? 
            post.getSkills().stream()
                .map(skill -> {
                    String skillPresignedUrl = null;
                    if (skill.getThumbnailUrl() != null && !skill.getThumbnailUrl().isEmpty()) {
                        skillPresignedUrl = fileStorageService.getPresignedUrl(skill.getThumbnailUrl());
                    }
                    return new SkillDto(
                        skill.getId(),
                        skill.getName(),
                        skill.getDescription(),
                        skill.getCategory(),
                        skillPresignedUrl,
                        skill.getDatePosted(),
                        skill.getLastUpdated()
                    );
                })
                .collect(Collectors.toSet()) : new HashSet<>();

        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory(),
                presignedUrl,
                post.getDatePosted(),
                post.getLastUpdated(),
                skillDtos // Appended to your updated PostDto constructor
        );
    }
}