package com.bennieslab.portfolio.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bennieslab.portfolio.dto.MediaPageDto;
import com.bennieslab.portfolio.service.FileStorageService;

/**
 * Backs the admin Media Library. Deliberately not whitelisted in
 * SecurityConfig — both endpoints require the same JWT auth as any other
 * admin/write operation, since listing the bucket exposes presigned URLs
 * for every stored file, thumbnail or model.
 */
@RestController
@RequestMapping("/media")
public class MediaController {

    private final FileStorageService fileStorageService;

    @Autowired
    public MediaController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * One page of files within a single category. category must be
     * "thumbnails" or "models" — there's no combined/"all categories" mode,
     * since S3 prefix listing is inherently per-prefix and combining them
     * would mean giving up true server-side pagination.
     */
    @GetMapping
    public ResponseEntity<?> listMedia(
            @RequestParam String category,
            @RequestParam(defaultValue = "24") int limit,
            @RequestParam(required = false) String continuationToken) {

        if (!"thumbnails".equals(category) && !"models".equals(category)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "category must be 'thumbnails' or 'models'");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        MediaPageDto page = fileStorageService.listFiles(category, limit, continuationToken);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMedia(@RequestParam("key") String key) {
        try {
            fileStorageService.deleteFile(key);
            return ResponseEntity.ok().build();
        } catch (IOException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Could not delete file: " + ex.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}