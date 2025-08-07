package com.bennieslab.portfolio.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bennieslab.portfolio.model.Post;
import com.bennieslab.portfolio.repository.PostRepository;
import com.bennieslab.portfolio.dto.PostDto;
import java.time.LocalDateTime;

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
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory(),
                presignedUrl,
                post.getDatePosted(),
                post.getLastUpdated()
        );
    }
}