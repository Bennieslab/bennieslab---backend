package com.bennieslab.portfolio.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String thumbnailUrl;
    private LocalDateTime datePosted;
    private LocalDateTime lastUpdated;
    private Set<SkillDto> skills = new HashSet<>(); // Added skills collection
    private boolean pinned;
    private int sortOrder;

    public PostDto(Long id, String title, String content, String category, 
                   String thumbnailUrl, LocalDateTime datePosted, LocalDateTime lastUpdated, 
                   Set<SkillDto> skills, boolean pinned, int sortOrder) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.datePosted = datePosted;
        this.lastUpdated = lastUpdated;
        this.skills = skills;
        this.pinned = pinned;
        this.sortOrder = sortOrder;
    }
}