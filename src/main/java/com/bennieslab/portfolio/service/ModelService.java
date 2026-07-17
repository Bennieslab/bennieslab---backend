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

import com.bennieslab.portfolio.model.Model;
import com.bennieslab.portfolio.model.Skill;
import com.bennieslab.portfolio.repository.ModelRepository;
import com.bennieslab.portfolio.dto.ModelDto;
import com.bennieslab.portfolio.dto.SkillDto;
import com.bennieslab.portfolio.repository.SkillRepository;
import com.bennieslab.portfolio.dto.ModelUpdateRequest;


@Service
public class ModelService {

    private final ModelRepository modelRepository;
    private final FileStorageService fileStorageService;
    private final SkillRepository skillRepository;

    @Autowired
    public ModelService(ModelRepository modelRepository, FileStorageService fileStorageService,
                        SkillRepository skillRepository) {
        this.modelRepository = modelRepository;
        this.fileStorageService = fileStorageService;
        this.skillRepository = skillRepository;
    }

    public Optional<ModelDto> getModelById(Long id) {
        return modelRepository.findById(id)
                .map(this::convertToDtoWithPresignedUrl);
    }

    /** Full list — used internally and by admin panel (no pagination). */
    public List<ModelDto> getAllModels() {
        return modelRepository.findAll().stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());
    }

    public List<ModelDto> getAllModels(String category, Long skillId) {
        return filterAndSortModels(category, skillId).stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());
    }

    /**
     * Paginated list — sorted by pinned DESC → sortOrder ASC → datePosted DESC.
     * Returns a Spring Page so the controller can forward totalPages/totalElements to the client.
     */
    public Page<ModelDto> getAllModels(int page, int size) {
        return getAllModels(page, size, null, null);
    }

    public Page<ModelDto> getAllModels(int page, int size, String category, Long skillId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Model> sortedModels = filterAndSortModels(category, skillId);

        int start = (int) pageRequest.getOffset();
        if (start >= sortedModels.size()) {
            return new PageImpl<>(List.of(), pageRequest, sortedModels.size());
        }

        int end = Math.min(start + pageRequest.getPageSize(), sortedModels.size());
        List<ModelDto> content = sortedModels.subList(start, end).stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, sortedModels.size());
    }

    public List<String> getAllCategories() {
        return modelRepository.findAll().stream()
                .map(Model::getCategory)
                .filter(category -> category != null && !category.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    public ModelDto addModel(ModelUpdateRequest request) {
        Model model = new Model();
        model.setName(request.getName());
        model.setDescription(request.getDescription());
        model.setCategory(request.getCategory());
        model.setThumbnailUrl(request.getThumbnailUrl());
        model.setModelFileKey(request.getModelFileKey());
        if (request.getPinned() != null)    model.setPinned(request.getPinned());
        if (request.getSortOrder() != null) model.setSortOrder(request.getSortOrder());

        if (request.getSkillIds() != null) {
            Set<Skill> resolvedSkills = new HashSet<>(skillRepository.findAllById(request.getSkillIds()));
            model.setSkills(resolvedSkills);
        }

        Model savedModel = modelRepository.save(model);
        return convertToDtoWithPresignedUrl(savedModel);
    }

    public ModelDto updateModel(Long id, ModelUpdateRequest updatedModel) {
        return modelRepository.findById(id)
                .map(model -> {
                    model.setName(updatedModel.getName());
                    model.setDescription(updatedModel.getDescription());
                    model.setCategory(updatedModel.getCategory());

                    if (updatedModel.getThumbnailUrl() != null) {
                        model.setThumbnailUrl(updatedModel.getThumbnailUrl());
                    }
                    if (updatedModel.getModelFileKey() != null) {
                        model.setModelFileKey(updatedModel.getModelFileKey());
                    }
                    if (updatedModel.getPinned() != null) {
                        model.setPinned(updatedModel.getPinned());
                    }
                    if (updatedModel.getSortOrder() != null) {
                        model.setSortOrder(updatedModel.getSortOrder());
                    }

                    if (updatedModel.getSkillIds() != null) {
                        Set<Skill> resolvedSkills = new HashSet<>(
                                skillRepository.findAllById(updatedModel.getSkillIds()));
                        model.setSkills(resolvedSkills);
                    }

                    model.setLastUpdated(LocalDateTime.now());
                    return convertToDtoWithPresignedUrl(modelRepository.save(model));
                })
                .orElseThrow(() -> new RuntimeException("Model not found with id " + id));
    }

    public void deleteModel(Long id) {
        modelRepository.deleteById(id);
    }

    private List<Model> filterAndSortModels(String category, Long skillId) {
        String normalizedCategory = category != null ? category.trim() : null;

        return modelRepository.findAll().stream()
                .filter(model -> matchesCategory(model, normalizedCategory))
                .filter(model -> matchesSkill(model, skillId))
                .sorted(Comparator.comparing(Model::isPinned).reversed()
                        .thenComparingInt(Model::getSortOrder)
                        .thenComparing(Model::getDatePosted, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private boolean matchesCategory(Model model, String category) {
        if (category == null || category.isBlank() || "all".equalsIgnoreCase(category)) {
            return true;
        }
        return model.getCategory() != null && model.getCategory().equalsIgnoreCase(category);
    }

    private boolean matchesSkill(Model model, Long skillId) {
        if (skillId == null) {
            return true;
        }
        return model.getSkills() != null && model.getSkills().stream()
                .anyMatch(skill -> skill.getId() != null && skill.getId().equals(skillId));
    }

    private ModelDto convertToDtoWithPresignedUrl(Model model) {
        String presignedThumbnailUrl = null;
        if (model.getThumbnailUrl() != null && !model.getThumbnailUrl().isEmpty()) {
            presignedThumbnailUrl = fileStorageService.getPresignedUrl(model.getThumbnailUrl());
        }

        String presignedModelUrl = null;
        if (model.getModelFileKey() != null && !model.getModelFileKey().isEmpty()) {
            presignedModelUrl = fileStorageService.getPresignedUrl(model.getModelFileKey());
        }

        // Cleanly map the internal Skill entities to safe, flat SkillDtos
        Set<SkillDto> skillDtos = model.getSkills() != null ?
            model.getSkills().stream()
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

        return new ModelDto(
                model.getId(),
                model.getName(),
                model.getDescription(),
                model.getCategory(),
                presignedThumbnailUrl,
                presignedModelUrl,
                model.getDatePosted(),
                model.getLastUpdated(),
                skillDtos,
                model.isPinned(),
                model.getSortOrder()
        );
    }
}