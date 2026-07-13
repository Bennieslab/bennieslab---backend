package com.bennieslab.portfolio.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.bennieslab.portfolio.model.Post;
import com.bennieslab.portfolio.repository.PostRepository;
import com.bennieslab.portfolio.dto.PostDto;
import com.bennieslab.portfolio.dto.SkillDto;

import com.bennieslab.portfolio.repository.SkillRepository;
import com.bennieslab.portfolio.dto.PostUpdateRequest;
import com.bennieslab.portfolio.model.Skill;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;
    private final SkillRepository skillRepository;

    @Autowired
    public PostService(PostRepository postRepository, FileStorageService fileStorageService,
                        SkillRepository skillRepository) {
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
        this.skillRepository = skillRepository;
    }

    public Optional<PostDto> getPostById(Long id) {
        return postRepository.findById(id)
                .map(this::convertToDtoWithPresignedUrl);
    }

    /** Full list — used internally and by admin panel (no pagination). */
    public List<PostDto> getPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());
    }

    /**
     * Paginated list — sorted by pinned DESC → sortOrder ASC → datePosted DESC.
     */
    public Page<PostDto> getPosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Post> sortedPosts = postRepository.findAll().stream()
                .sorted(Comparator.comparing(Post::isPinned).reversed()
                        .thenComparingInt(Post::getSortOrder)
                        .thenComparing(Post::getDatePosted, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        int start = (int) pageRequest.getOffset();
        if (start >= sortedPosts.size()) {
            return new PageImpl<>(List.of(), pageRequest, sortedPosts.size());
        }

        int end = Math.min(start + pageRequest.getPageSize(), sortedPosts.size());
        List<PostDto> content = sortedPosts.subList(start, end).stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, sortedPosts.size());
    }

    public PostDto createPost(PostUpdateRequest request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getPinned() != null)    post.setPinned(request.getPinned());
        if (request.getSortOrder() != null) post.setSortOrder(request.getSortOrder());

        if (request.getSkillIds() != null) {
            Set<Skill> resolvedSkills = new HashSet<>(skillRepository.findAllById(request.getSkillIds()));
            post.setSkills(resolvedSkills);
        }

        Post savedPost = postRepository.save(post);
        return convertToDtoWithPresignedUrl(savedPost);
    }

    public PostDto updatePost(Long id, PostUpdateRequest updatedPost) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setTitle(updatedPost.getTitle());
                    post.setContent(updatedPost.getContent());
                    post.setCategory(updatedPost.getCategory());

                    if (updatedPost.getThumbnailUrl() != null) {
                        post.setThumbnailUrl(updatedPost.getThumbnailUrl());
                    }
                    if (updatedPost.getPinned() != null) {
                        post.setPinned(updatedPost.getPinned());
                    }
                    if (updatedPost.getSortOrder() != null) {
                        post.setSortOrder(updatedPost.getSortOrder());
                    }

                    // Only touch skills if the client explicitly sent skillIds.
                    // null means "leave existing associations alone"; an empty
                    // set is an explicit "clear all skills".
                    if (updatedPost.getSkillIds() != null) {
                        Set<Skill> resolvedSkills = new HashSet<>(
                                skillRepository.findAllById(updatedPost.getSkillIds()));
                        post.setSkills(resolvedSkills);
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
                        skill.getLastUpdated(),
                        skill.isPinned(),
                        skill.getSortOrder()
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
                skillDtos,
                post.isPinned(),
                post.getSortOrder()
        );
    }
}
