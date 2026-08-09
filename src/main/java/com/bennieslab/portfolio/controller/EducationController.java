package com.bennieslab.portfolio.controller;

import com.bennieslab.portfolio.model.Education;
import com.bennieslab.portfolio.service.EducationService;
import com.bennieslab.portfolio.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "https://bennieslab.github.io")@RestController
@RequestMapping("/education")
public class EducationController {

    @Autowired
    private EducationService educationService;

    @GetMapping
    public List<Education> getAllEducation() {
        return educationService.getAllEducation();
    }

    @GetMapping("/{id}")
    public Education getEducationById(@PathVariable Long id) {
        return educationService.getEducationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found with ID: " + id));
    }

    @PostMapping
    public Education createEducation(@RequestBody Education education) {
        return educationService.createEducation(education);
    }

    @PutMapping("/{id}")
    public Education updateEducation(@PathVariable Long id, @RequestBody Education updated) {
        return educationService.updateEducation(id, updated);
    }

    @DeleteMapping("/{id}")
    public String deleteEducation(@PathVariable Long id) {
        educationService.deleteEducation(id);
        return "Education with ID " + id + " deleted successfully.";
    }
}
