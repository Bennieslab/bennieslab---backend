package com.bennieslab.portfolio.controller;

import java.util.List;
import java.util.Optional;

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

import com.bennieslab.portfolio.repository.mini.ProjectMini;
import com.bennieslab.portfolio.service.ProjectService;
import com.bennieslab.portfolio.dto.ProjectDto;
import com.bennieslab.portfolio.dto.ProjectUpdateRequest;

@CrossOrigin(origins = "https://bennieslab.github.io")
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{id}")
    public Optional<ProjectDto> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    /**
     * If ?page and ?size are both supplied, returns a Spring Page<ProjectDto> (with
     * totalPages, totalElements, content[] etc.) sorted by the smart sort chain.
     * Otherwise returns the full List<ProjectDto> for backward compatibility
     * (used by admin.js and any other callers that don't paginate).
     */
    @GetMapping
    public ResponseEntity<?> getAllProjects(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long skillId) {
        if (page != null && size != null) {
            Page<ProjectDto> result = projectService.getAllProjects(page, size, category, skillId);
            return ResponseEntity.ok(result);
        }
        List<ProjectDto> result = projectService.getAllProjects(category, skillId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/categories")
    public List<String> getAllCategories() {
        return projectService.getAllCategories();
    }

    @GetMapping("/names")
    public List<ProjectMini> getAllProjectNames() {
        return projectService.getAllProjectNames();
    }

    @PostMapping
    public ProjectDto createProject(@RequestBody ProjectUpdateRequest project) {
        return projectService.addProject(project);
    }

    @PutMapping("/{id}")
    public ProjectDto updateProject(@PathVariable Long id, @RequestBody ProjectUpdateRequest updatedProject) {
        return projectService.updateProject(id, updatedProject);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}
