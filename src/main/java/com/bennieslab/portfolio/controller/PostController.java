package com.bennieslab.portfolio.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bennieslab.portfolio.service.PostService;
import com.bennieslab.portfolio.dto.PostDto;
import com.bennieslab.portfolio.dto.PostUpdateRequest;

@CrossOrigin(origins = "https://bennieslab.github.io")
@RestController
@RequestMapping("/blog")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public Optional<PostDto> getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    /**
     * If ?page and ?size are both supplied, returns a Spring Page<PostDto> sorted
     * by the smart sort chain. Otherwise returns the full List<PostDto>.
     */
    @GetMapping
    public ResponseEntity<?> getPosts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Page<PostDto> result = postService.getPosts(page, size);
            return ResponseEntity.ok(result);
        }
        List<PostDto> result = postService.getPosts();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public PostDto createPost(@RequestBody PostUpdateRequest post) {
        return postService.createPost(post);
    }

    @PutMapping("/{id}")
    public PostDto updatePost(@PathVariable Long id, @RequestBody PostUpdateRequest updatedPost) {
        return postService.updatePost(id, updatedPost);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        postService.deletePost(id);
    }
}