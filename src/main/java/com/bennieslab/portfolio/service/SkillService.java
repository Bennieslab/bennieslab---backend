package com.bennieslab.portfolio.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.bennieslab.portfolio.model.Skill;
import com.bennieslab.portfolio.repository.SkillRepository;
import com.bennieslab.portfolio.dto.SkillDto;
import com.bennieslab.portfolio.dto.SkillUpdateRequest;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public SkillService(SkillRepository skillRepository, FileStorageService fileStorageService) {
        this.skillRepository = skillRepository;
        this.fileStorageService = fileStorageService;
    }

    public Optional<SkillDto> getSkillById(Long id) {
        return skillRepository.findById(id)
                .map(this::convertToDtoWithPresignedUrl);
    }

    /** Full list — used internally and by admin panel (no pagination). */
    public List<SkillDto> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());
    }

    /**
     * Paginated list — sorted by pinned DESC → sortOrder ASC → name ASC.
     */
    public Page<SkillDto> getAllSkills(int page, int size) {
        return skillRepository.findAllSorted(PageRequest.of(page, size))
                .map(this::convertToDtoWithPresignedUrl);
    }

    public SkillDto addSkill(SkillUpdateRequest request) {
        Skill skill = new Skill();
        skill.setName(request.getName());
        skill.setDescription(request.getDescription());
        skill.setCategory(request.getCategory());
        skill.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getPinned() != null)    skill.setPinned(request.getPinned());
        if (request.getSortOrder() != null) skill.setSortOrder(request.getSortOrder());

        Skill savedSkill = skillRepository.save(skill);
        return convertToDtoWithPresignedUrl(savedSkill);
    }

    public SkillDto updateSkill(Long id, SkillUpdateRequest updatedSkill) {
        return skillRepository.findById(id)
                .map(skill -> {
                    skill.setName(updatedSkill.getName());
                    skill.setDescription(updatedSkill.getDescription());
                    skill.setCategory(updatedSkill.getCategory());
                    if (updatedSkill.getThumbnailUrl() != null) {
                        skill.setThumbnailUrl(updatedSkill.getThumbnailUrl());
                    }
                    if (updatedSkill.getPinned() != null) {
                        skill.setPinned(updatedSkill.getPinned());
                    }
                    if (updatedSkill.getSortOrder() != null) {
                        skill.setSortOrder(updatedSkill.getSortOrder());
                    }
                    skill.setLastUpdated(LocalDateTime.now());
                    return convertToDtoWithPresignedUrl(skillRepository.save(skill));
                })
                .orElseThrow(() -> new RuntimeException("Skill not found with id " + id));
    }

    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    private SkillDto convertToDtoWithPresignedUrl(Skill skill) {
        String presignedUrl = null;
        if (skill.getThumbnailUrl() != null && !skill.getThumbnailUrl().isEmpty()) {
            presignedUrl = fileStorageService.getPresignedUrl(skill.getThumbnailUrl());
        }
        return new SkillDto(
                skill.getId(),
                skill.getName(),
                skill.getDescription(),
                skill.getCategory(),
                presignedUrl,
                skill.getDatePosted(),
                skill.getLastUpdated(),
                skill.isPinned(),
                skill.getSortOrder()
        );
    }
}