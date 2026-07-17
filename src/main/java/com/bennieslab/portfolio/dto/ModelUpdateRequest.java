package com.bennieslab.portfolio.dto;

import java.util.List;

public class ModelUpdateRequest {

    private String name;
    private String description;
    private String category;
    private String thumbnailUrl;
    private String modelFileKey;
    private Boolean pinned;
    private Integer sortOrder;
    private List<Long> skillIds;

    public ModelUpdateRequest() {}

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

    /** Raw storage key (e.g. "models/<uuid>") returned by POST /upload/model. */
    public String getModelFileKey() {
        return modelFileKey;
    }

    public void setModelFileKey(String modelFileKey) {
        this.modelFileKey = modelFileKey;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<Long> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<Long> skillIds) {
        this.skillIds = skillIds;
    }
}