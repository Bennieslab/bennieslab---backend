package com.bennieslab.portfolio.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class ModelDto {

    private Long id;
    private String name;
    private String description;
    private String category;
    private String thumbnailUrl;
    private String modelUrl;
    private LocalDateTime datePosted;
    private LocalDateTime lastUpdated;
    private Set<SkillDto> skills;
    private boolean pinned;
    private int sortOrder;

    public ModelDto() {}

    public ModelDto(Long id, String name, String description, String category,
                     String thumbnailUrl, String modelUrl,
                     LocalDateTime datePosted, LocalDateTime lastUpdated,
                     Set<SkillDto> skills, boolean pinned, int sortOrder) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.modelUrl = modelUrl;
        this.datePosted = datePosted;
        this.lastUpdated = lastUpdated;
        this.skills = skills;
        this.pinned = pinned;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /** Presigned, directly-fetchable URL to the .glb/.gltf asset — what the three.js viewer loads. */
    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Set<SkillDto> getSkills() {
        return skills;
    }

    public void setSkills(Set<SkillDto> skills) {
        this.skills = skills;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}