package com.bennieslab.portfolio.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

public class ProjectDto {

    private Long id;
    private String name;
    private String description;
    private String category;
    private String thumbnailUrl;
    private LocalDateTime datePosted;
    private LocalDateTime lastUpdated;
    private Set<SkillDto> skills = new HashSet<>(); // Added skills collection

    public ProjectDto() {}

    public ProjectDto(Long id, String name, String description, String category, 
                      String thumbnailUrl, LocalDateTime datePosted, LocalDateTime lastUpdated, 
                      Set<SkillDto> skills) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.datePosted = datePosted;
        this.lastUpdated = lastUpdated;
        this.skills = skills;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public LocalDateTime getDatePosted() { return datePosted; }
    public void setDatePosted(LocalDateTime datePosted) { this.datePosted = datePosted; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public Set<SkillDto> getSkills() { return skills; }
    public void setSkills(Set<SkillDto> skills) { this.skills = skills; }
}