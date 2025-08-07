package com.bennieslab.portfolio.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bennieslab.portfolio.model.Skill;
import com.bennieslab.portfolio.repository.SkillRepository;
import com.bennieslab.portfolio.dto.SkillDto;

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

    public List<SkillDto> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());
    }

    public SkillDto addSkill(Skill skill) {
        Skill savedSkill = skillRepository.save(skill);
        return convertToDtoWithPresignedUrl(savedSkill);
    }

    public SkillDto updateSkill(Long id, Skill updatedSkill) {
        return skillRepository.findById(id)
                .map(skill -> {
                    skill.setName(updatedSkill.getName());
                    skill.setDescription(updatedSkill.getDescription());
                    skill.setCategory(updatedSkill.getCategory());
                    if (updatedSkill.getThumbnailUrl() != null) {
                        skill.setThumbnailUrl(updatedSkill.getThumbnailUrl());
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
                skill.getLastUpdated()
        );
    }
}