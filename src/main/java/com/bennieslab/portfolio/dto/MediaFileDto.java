package com.bennieslab.portfolio.dto;

import java.time.Instant;

public class MediaFileDto {

    private String key;
    private String url;
    private long sizeBytes;
    private Instant lastModified;
    /** "thumbnails", "models", or "other" — derived from the key's prefix. */
    private String category;

    public MediaFileDto() {
    }

    public MediaFileDto(String key, String url, long sizeBytes, Instant lastModified, String category) {
        this.key = key;
        this.url = url;
        this.sizeBytes = sizeBytes;
        this.lastModified = lastModified;
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}