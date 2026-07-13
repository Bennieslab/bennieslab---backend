package com.bennieslab.portfolio.dto;

import java.util.Set;

public class PostUpdateRequest {

    private String title;
    private String content;
    private String category;
    private String thumbnailUrl;
    private Set<Long> skillIds; // null = "don't touch skills"; [] = "clear skills"
    private Boolean pinned;    // null = "don't touch pinned"
    private Integer sortOrder; // null = "don't touch sortOrder"

    public PostUpdateRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Set<Long> getSkillIds() { return skillIds; }
    public void setSkillIds(Set<Long> skillIds) { this.skillIds = skillIds; }

    public Boolean getPinned() { return pinned; }
    public void setPinned(Boolean pinned) { this.pinned = pinned; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}