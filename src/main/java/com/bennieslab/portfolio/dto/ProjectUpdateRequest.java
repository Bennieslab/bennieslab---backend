package com.bennieslab.portfolio.dto;

import java.util.Set;

public class ProjectUpdateRequest {

    private String name;
    private String description;
    private String category;
    private String thumbnailUrl;
    private Set<Long> skillIds; // null = "don't touch skills"; [] = "clear skills"

    public ProjectUpdateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Set<Long> getSkillIds() { return skillIds; }
    public void setSkillIds(Set<Long> skillIds) { this.skillIds = skillIds; }
}