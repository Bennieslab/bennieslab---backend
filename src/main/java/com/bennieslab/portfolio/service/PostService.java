package com.bennieslab.portfolio.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bennieslab.portfolio.model.Post;
import com.bennieslab.portfolio.repository.PostRepository;
import com.bennieslab.portfolio.dto.PostDto;
import com.bennieslab.portfolio.dto.SkillDto;

import com.bennieslab.portfolio.repository.SkillRepository;
import com.bennieslab.portfolio.dto.PostUpdateRequest;
import com.bennieslab.portfolio.model.Skill;
import com.bennieslab.portfolio.exception.ResourceNotFoundException;
import com.bennieslab.portfolio.specification.ContentSpecifications;

/**
 * Smart sort chain used for list endpoints:
 * pinned DESC → sortOrder ASC → datePosted DESC.
 * Applied as an SQL ORDER BY for real DB-side pagination.
 */
@Service
public class PostService {

    private static final Sort SMART_SORT = Sort.by(
            Sort.Order.desc("pinned"),
            Sort.Order.asc("sortOrder"),
            Sort.Order.desc("datePosted")
    );

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
     * Full filtered list — category + skill filters, smart-sorted.
     * Used by admin panel and non-paginated callers.
     */
    public List<PostDto> getPosts(String category, Long skillId) {
        Specification<Post> spec = buildSpecification(category, skillId);
        return postRepository.findAll(spec).stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());
    }

    /**
     * Paginated list — DB-side filter + sort, then slice via PageRequest.
     * Only fetches the requested page from the database.
     */
    public Page<PostDto> getPosts(int page, int size, String category, Long skillId) {
        Pageable pageable = PageRequest.of(page, size, SMART_SORT);
        Specification<Post> spec = buildSpecification(category, skillId);
        return postRepository.findAll(spec, pageable)
                .map(this::convertToDtoWithPresignedUrl);
    }

    /** Convenience overload without filters. */
    public Page<PostDto> getPosts(int page, int size) {
        return getPosts(page, size, null, null);
    }

    public List<String> getAllCategories() {
        return postRepository.findAll().stream()
                .map(Post::getCategory)
                .filter(category -> category != null && !category.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
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
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + id));
    }

    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id " + id);
        }
        postRepository.deleteById(id);
    }

    private Specification<Post> buildSpecification(String category, Long skillId) {
        return ContentSpecifications.<Post>categoryIs(category, "category")
                .and(ContentSpecifications.<Post>skillIdIs(skillId));
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