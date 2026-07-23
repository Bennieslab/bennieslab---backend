package com.bennieslab.portfolio.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bennieslab.portfolio.dto.MediaFileDto;
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

    @GetMapping
    public List<MediaFileDto> listMedia() {
        return fileStorageService.listAllFiles();
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