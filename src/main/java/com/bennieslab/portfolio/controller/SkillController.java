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

import com.bennieslab.portfolio.service.SkillService;
import com.bennieslab.portfolio.dto.SkillDto;
import com.bennieslab.portfolio.dto.SkillUpdateRequest;

@CrossOrigin(origins = "https://bennieslab.github.io")
@RestController
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/{id}")
    public Optional<SkillDto> getSkillById(@PathVariable Long id) {
        return skillService.getSkillById(id);
    }

    /**
     * If ?page and ?size are both supplied, returns a Spring Page<SkillDto> sorted
     * by pinned DESC → sortOrder ASC → name ASC. Otherwise returns the full list.
     */
    @GetMapping
    public ResponseEntity<?> getAllSkills(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Page<SkillDto> result = skillService.getAllSkills(page, size);
            return ResponseEntity.ok(result);
        }
        List<SkillDto> result = skillService.getAllSkills();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public SkillDto createSkill(@RequestBody SkillUpdateRequest skill) {
        return skillService.addSkill(skill);
    }

    @PutMapping("/{id}")
    public SkillDto updateSkill(@PathVariable Long id, @RequestBody SkillUpdateRequest updatedSkill) {
        return skillService.updateSkill(id, updatedSkill);
    }

    @DeleteMapping("/{id}")
    public void deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
    }
}