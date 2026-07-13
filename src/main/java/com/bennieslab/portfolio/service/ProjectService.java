package com.bennieslab.portfolio.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.bennieslab.portfolio.model.Project;
import com.bennieslab.portfolio.model.Skill;
import com.bennieslab.portfolio.repository.ProjectRepository;
import com.bennieslab.portfolio.repository.mini.ProjectMini;
import com.bennieslab.portfolio.dto.ProjectDto;
import com.bennieslab.portfolio.dto.SkillDto;
import com.bennieslab.portfolio.repository.SkillRepository;
import com.bennieslab.portfolio.dto.ProjectUpdateRequest;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;
    private final SkillRepository skillRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, FileStorageService fileStorageService,
                        SkillRepository skillRepository) {
        this.projectRepository = projectRepository;
        this.fileStorageService = fileStorageService;
        this.skillRepository = skillRepository;
    }

    public Optional<ProjectDto> getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(this::convertToDtoWithPresignedUrl);
    }

    /** Full list — used internally and by admin panel (no pagination). */
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());
    }

    /**
     * Paginated list — sorted by pinned DESC → sortOrder ASC → datePosted DESC.
     * Returns a Spring Page so the controller can forward totalPages/totalElements to the client.
     */
    public Page<ProjectDto> getAllProjects(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Project> sortedProjects = projectRepository.findAll().stream()
                .sorted(Comparator.comparing(Project::isPinned).reversed()
                        .thenComparingInt(Project::getSortOrder)
                        .thenComparing(Project::getDatePosted, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        int start = (int) pageRequest.getOffset();
        if (start >= sortedProjects.size()) {
            return new PageImpl<>(List.of(), pageRequest, sortedProjects.size());
        }

        int end = Math.min(start + pageRequest.getPageSize(), sortedProjects.size());
        List<ProjectDto> content = sortedProjects.subList(start, end).stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, sortedProjects.size());
    }

    public List<ProjectMini> getAllProjectNames() {
        return projectRepository.findAllProjectMini();
    }

    public ProjectDto addProject(ProjectUpdateRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setCategory(request.getCategory());
        project.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getPinned() != null)    project.setPinned(request.getPinned());
        if (request.getSortOrder() != null) project.setSortOrder(request.getSortOrder());

        if (request.getSkillIds() != null) {
            Set<Skill> resolvedSkills = new HashSet<>(skillRepository.findAllById(request.getSkillIds()));
            project.setSkills(resolvedSkills);
        }

        Project savedProject = projectRepository.save(project);
        return convertToDtoWithPresignedUrl(savedProject);
    }

    public ProjectDto updateProject(Long id, ProjectUpdateRequest updatedProject) {
        return projectRepository.findById(id)
                .map(project -> {
                    project.setName(updatedProject.getName());
                    project.setDescription(updatedProject.getDescription());
                    project.setCategory(updatedProject.getCategory());

                    if (updatedProject.getThumbnailUrl() != null) {
                        project.setThumbnailUrl(updatedProject.getThumbnailUrl());
                    }
                    if (updatedProject.getPinned() != null) {
                        project.setPinned(updatedProject.getPinned());
                    }
                    if (updatedProject.getSortOrder() != null) {
                        project.setSortOrder(updatedProject.getSortOrder());
                    }

                    if (updatedProject.getSkillIds() != null) {
                        Set<Skill> resolvedSkills = new HashSet<>(
                                skillRepository.findAllById(updatedProject.getSkillIds()));
                        project.setSkills(resolvedSkills);
                    }

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
                        skill.getLastUpdated(),
                        skill.isPinned(),
                        skill.getSortOrder()
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
                skillDtos,
                project.isPinned(),
                project.getSortOrder()
        );
    }
}
