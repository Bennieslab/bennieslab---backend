package com.bennieslab.portfolio.dto;

import java.util.Set;

public class SkillUpdateRequest {

    private String name;
    private String description;
    private String category;
    private String thumbnailUrl;
    private Boolean pinned;    // null = "don't touch pinned"
    private Integer sortOrder; // null = "don't touch sortOrder"

    public SkillUpdateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Boolean getPinned() { return pinned; }
    public void setPinned(Boolean pinned) { this.pinned = pinned; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
