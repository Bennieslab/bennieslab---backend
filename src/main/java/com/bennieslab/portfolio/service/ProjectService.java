package com.bennieslab.portfolio.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bennieslab.portfolio.model.Project;
import com.bennieslab.portfolio.repository.ProjectRepository;
import com.bennieslab.portfolio.repository.mini.ProjectMini; 
import com.bennieslab.portfolio.dto.ProjectDto; 

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService; 

    @Autowired
    public ProjectService(ProjectRepository projectRepository, FileStorageService fileStorageService) {
        this.projectRepository = projectRepository;
        this.fileStorageService = fileStorageService;
    }

    public Optional<ProjectDto> getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(this::convertToDtoWithPresignedUrl); 
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToDtoWithPresignedUrl) 
                .collect(Collectors.toList());
    }

    public List<ProjectMini> getAllProjectNames() {
        return projectRepository.findAllProjectMini();
    }

    public ProjectDto addProject(Project project) {
        Project savedProject = projectRepository.save(project);
        return convertToDtoWithPresignedUrl(savedProject); 
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    private ProjectDto convertToDtoWithPresignedUrl(Project project) {
        String presignedUrl = null;
        if (project.getThumbnailUrl() != null && !project.getThumbnailUrl().isEmpty()) {
            presignedUrl = fileStorageService.getPresignedUrl(project.getThumbnailUrl());
        }
        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCategory(),
                presignedUrl, 
                project.getDatePosted(),
                project.getLastUpdated()
        );
    }
}