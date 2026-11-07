package com.bennieslab.portfolio.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bennieslab.portfolio.dto.ReleaseDto;
import com.bennieslab.portfolio.dto.ReleaseUpdateRequest;
import com.bennieslab.portfolio.exception.ResourceNotFoundException;
import com.bennieslab.portfolio.model.Release;
import com.bennieslab.portfolio.repository.ReleaseRepository;
import com.bennieslab.portfolio.specification.ContentSpecifications;

@Service
public class ReleaseService {

    private static final Sort SMART_SORT = Sort.by(
            Sort.Order.desc("pinned"),
            Sort.Order.asc("sortOrder"),
            Sort.Order.desc("datePosted")
    );

    private final ReleaseRepository releaseRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public ReleaseService(ReleaseRepository releaseRepository, FileStorageService fileStorageService) {
        this.releaseRepository = releaseRepository;
        this.fileStorageService = fileStorageService;
    }

    public Optional<ReleaseDto> getReleaseById(Long id) {
        return releaseRepository.findById(id)
                .map(this::convertToDtoWithPresignedUrl);
    }

    public List<ReleaseDto> getAllReleases() {
        return releaseRepository.findAllByOrderByPinnedDescSortOrderAscDatePostedDesc()
                .stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());
    }

    public List<ReleaseDto> getAllReleases(String category) {
        if (category == null || category.equalsIgnoreCase("all") || category.isBlank()) {
            return getAllReleases();
        }
        Specification<Release> spec = ContentSpecifications.categoryIs(category, "category");
        return releaseRepository.findAll(spec, SMART_SORT)
                .stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());
    }

    public Page<ReleaseDto> getAllReleases(int page, int size, String category) {
        Pageable pageable = PageRequest.of(page, size, SMART_SORT);
        Specification<Release> spec = ContentSpecifications.categoryIs(category, "category");
        return releaseRepository.findAll(spec, pageable)
                .map(this::convertToDtoWithPresignedUrl);
    }

    public ReleaseDto addRelease(ReleaseUpdateRequest request) {
        Release release = new Release();
        release.setName(request.getName());
        release.setVersion(request.getVersion() != null ? request.getVersion() : "v1.0.0");
        release.setCategory(request.getCategory());
        release.setStatus(request.getStatus() != null ? request.getStatus() : "Stable");
        release.setDescription(request.getDescription());
        release.setDownloadUrl(request.getDownloadUrl());
        release.setGithubUrl(request.getGithubUrl());
        release.setThumbnailUrl(request.getThumbnailUrl());
        release.setPlatforms(request.getPlatforms());
        if (request.getPinned() != null) {
            release.setPinned(request.getPinned());
        }
        if (request.getSortOrder() != null) {
            release.setSortOrder(request.getSortOrder());
        }

        Release saved = releaseRepository.save(release);
        return convertToDtoWithPresignedUrl(saved);
    }

    public ReleaseDto updateRelease(Long id, ReleaseUpdateRequest request) {
        return releaseRepository.findById(id)
                .map(release -> {
                    if (request.getName() != null) release.setName(request.getName());
                    if (request.getVersion() != null) release.setVersion(request.getVersion());
                    if (request.getCategory() != null) release.setCategory(request.getCategory());
                    if (request.getStatus() != null) release.setStatus(request.getStatus());
                    if (request.getDescription() != null) release.setDescription(request.getDescription());
                    if (request.getDownloadUrl() != null) release.setDownloadUrl(request.getDownloadUrl());
                    if (request.getGithubUrl() != null) release.setGithubUrl(request.getGithubUrl());
                    if (request.getThumbnailUrl() != null) release.setThumbnailUrl(request.getThumbnailUrl());
                    if (request.getPlatforms() != null) release.setPlatforms(request.getPlatforms());
                    if (request.getPinned() != null) release.setPinned(request.getPinned());
                    if (request.getSortOrder() != null) release.setSortOrder(request.getSortOrder());

                    release.setLastUpdated(LocalDateTime.now());
                    return convertToDtoWithPresignedUrl(releaseRepository.save(release));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Release not found with id " + id));
    }

    public void deleteRelease(Long id) {
        if (!releaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Release not found with id " + id);
        }
        releaseRepository.deleteById(id);
    }

    private ReleaseDto convertToDtoWithPresignedUrl(Release release) {
        String presignedUrl = null;
        if (release.getThumbnailUrl() != null && !release.getThumbnailUrl().isBlank()) {
            presignedUrl = fileStorageService.getPresignedUrl(release.getThumbnailUrl());
        }

        return new ReleaseDto(
                release.getId(),
                release.getName(),
                release.getVersion(),
                release.getCategory(),
                release.getStatus(),
                release.getDescription(),
                release.getDownloadUrl(),
                release.getGithubUrl(),
                presignedUrl != null ? presignedUrl : release.getThumbnailUrl(),
                release.getPlatforms(),
                release.isPinned(),
                release.getSortOrder(),
                release.getDatePosted(),
                release.getLastUpdated()
        );
    }
}
