package com.bennieslab.portfolio.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor

public class PostDto {
    private Long id;
    private String title;
    private String content; // Assuming you want to send the full content
    private String category;
    private String thumbnailUrl; // This will hold the pre-signed URL for the frontend
    private LocalDateTime datePosted;
    private LocalDateTime lastUpdated;

    // A constructor to easily convert a Post entity to a PostDto
    public PostDto(Long id, String title, String content, String category, String thumbnailUrl, LocalDateTime datePosted, LocalDateTime lastUpdated) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl; // This is where the pre-signed URL will be set
        this.datePosted = datePosted;
        this.lastUpdated = lastUpdated;
    }
}
