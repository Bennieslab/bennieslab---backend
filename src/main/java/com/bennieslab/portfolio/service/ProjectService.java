package com.bennieslab.portfolio.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bennieslab.portfolio.model.Project;
import com.bennieslab.portfolio.repository.ProjectRepository;
import com.bennieslab.portfolio.repository.mini.ProjectMini;
import com.bennieslab.portfolio.dto.ProjectDto;
import com.bennieslab.portfolio.dto.SkillDto;

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

    public ProjectDto updateProject(Long id, Project updatedProject) {
        return projectRepository.findById(id)
                .map(project -> {
                    project.setName(updatedProject.getName());
                    project.setDescription(updatedProject.getDescription());
                    project.setCategory(updatedProject.getCategory());
                    
                    // Update thumbnail if provided
                    if (updatedProject.getThumbnailUrl() != null) {
                        project.setThumbnailUrl(updatedProject.getThumbnailUrl());
                    }
                    
                    // Sync technical skill tags
                    project.setSkills(updatedProject.getSkills());
                    
                    project.setLastUpdated(LocalDateTime.now());
                    return convertToDtoWithPresignedUrl(projectRepository.save(project));
                })
                .orElseThrow(() -> new RuntimeException("Project not found with id " + id));
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    private ProjectDto convertToDtoWithPresignedUrl(Project project) {
        String presignedUrl = null;
        if (project.getThumbnailUrl() != null && !project.getThumbnailUrl().isEmpty()) {
            presignedUrl = fileStorageService.getPresignedUrl(project.getThumbnailUrl());
        }

        // Cleanly map the internal Skill entities to safe, flat SkillDtos
        Set<SkillDto> skillDtos = project.getSkills() != null ? 
            project.getSkills().stream()
                .map(skill -> {
                    String skillPresignedUrl = null;
                    if (skill.getThumbnailUrl() != null && !skill.getThumbnailUrl().isEmpty()) {
                        skillPresignedUrl = fileStorageService.getPresignedUrl(skill.getThumbnailUrl());
                    }
                    return new SkillDto(
                        skill.getId(),
                        skill.getName(),
                        skill.getDescription(),
                        skill.getCategory(),
                        skillPresignedUrl,
                        skill.getDatePosted(),
                        skill.getLastUpdated()
                    );
                })
                .collect(Collectors.toSet()) : new HashSet<>();

        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCategory(),
                presignedUrl,
                project.getDatePosted(),
                project.getLastUpdated(),
                skillDtos // Appended to your updated ProjectDto constructor
        );
    }
}