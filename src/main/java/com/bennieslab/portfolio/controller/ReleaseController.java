package com.bennieslab.portfolio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bennieslab.portfolio.dto.ReleaseDto;
import com.bennieslab.portfolio.dto.ReleaseUpdateRequest;
import com.bennieslab.portfolio.exception.ResourceNotFoundException;
import com.bennieslab.portfolio.service.ReleaseService;

@CrossOrigin(origins = "https://bennieslab.github.io")
@RestController
@RequestMapping("/releases")
public class ReleaseController {

    private final ReleaseService releaseService;

    @Autowired
    public ReleaseController(ReleaseService releaseService) {
        this.releaseService = releaseService;
    }

    @GetMapping("/{id}")
    public ReleaseDto getReleaseById(@PathVariable Long id) {
        return releaseService.getReleaseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Release not found with id " + id));
    }

    @GetMapping
    public ResponseEntity<?> getReleases(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String category) {
        if (page != null && size != null) {
            Page<ReleaseDto> result = releaseService.getAllReleases(page, size, category);
            return ResponseEntity.ok(result);
        }
        List<ReleaseDto> result = releaseService.getAllReleases(category);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ReleaseDto createRelease(@RequestBody ReleaseUpdateRequest release) {
        return releaseService.addRelease(release);
    }

    @PutMapping("/{id}")
    public ReleaseDto updateRelease(@PathVariable Long id, @RequestBody ReleaseUpdateRequest updatedRelease) {
        return releaseService.updateRelease(id, updatedRelease);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelease(@PathVariable Long id) {
        releaseService.deleteRelease(id);
        return ResponseEntity.noContent().build();
    }
}
