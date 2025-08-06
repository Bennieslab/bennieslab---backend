package com.bennieslab.portfolio.dto;

import java.time.LocalDateTime;

public class SkillDto {

    private Long id;
    private String name;
    private String description;
    private String category;
    private String thumbnailUrl;
    private LocalDateTime datePosted;
    private LocalDateTime lastUpdated;

    public SkillDto() {
    }

    public SkillDto(Long id, String name, String description, String category, String thumbnailUrl, LocalDateTime datePosted, LocalDateTime lastUpdated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.datePosted = datePosted;
        this.lastUpdated = lastUpdated;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
