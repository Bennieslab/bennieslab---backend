package com.bennieslab.portfolio.dto;

import java.util.List;

public class MediaPageDto {

    private List<MediaFileDto> files;
    private String nextContinuationToken;
    private boolean hasMore;

    public MediaPageDto() {}

    public MediaPageDto(List<MediaFileDto> files, String nextContinuationToken, boolean hasMore) {
        this.files = files;
        this.nextContinuationToken = nextContinuationToken;
        this.hasMore = hasMore;
    }

    public List<MediaFileDto> getFiles() {
        return files;
    }

    public void setFiles(List<MediaFileDto> files) {
        this.files = files;
    }

    public String getNextContinuationToken() {
        return nextContinuationToken;
    }

    public void setNextContinuationToken(String nextContinuationToken) {
        this.nextContinuationToken = nextContinuationToken;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}