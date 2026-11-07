package com.bennieslab.portfolio.dto;

import java.time.LocalDateTime;

public class ReleaseDto {

    private Long id;
    private String name;
    private String version;
    private String category;
    private String status;
    private String description;
    private String downloadUrl;
    private String githubUrl;
    private String thumbnailUrl;
    private String platforms;
    private boolean pinned;
    private int sortOrder;
    private LocalDateTime datePosted;
    private LocalDateTime lastUpdated;

    public ReleaseDto() {}

    public ReleaseDto(Long id, String name, String version, String category, String status,
                      String description, String downloadUrl, String githubUrl, String thumbnailUrl,
                      String platforms, boolean pinned, int sortOrder, LocalDateTime datePosted,
                      LocalDateTime lastUpdated) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.category = category;
        this.status = status;
        this.description = description;
        this.downloadUrl = downloadUrl;
        this.githubUrl = githubUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.platforms = platforms;
        this.pinned = pinned;
        this.sortOrder = sortOrder;
        this.datePosted = datePosted;
        this.lastUpdated = lastUpdated;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String platforms) {
        this.platforms = platforms;
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
}
