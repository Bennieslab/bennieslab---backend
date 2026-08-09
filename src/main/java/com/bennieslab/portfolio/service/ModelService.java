package com.bennieslab.portfolio.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bennieslab.portfolio.model.Model;
import com.bennieslab.portfolio.model.Skill;
import com.bennieslab.portfolio.repository.ModelRepository;
import com.bennieslab.portfolio.dto.ModelDto;
import com.bennieslab.portfolio.dto.SkillDto;
import com.bennieslab.portfolio.repository.SkillRepository;
import com.bennieslab.portfolio.dto.ModelUpdateRequest;
import com.bennieslab.portfolio.exception.ResourceNotFoundException;
import com.bennieslab.portfolio.specification.ContentSpecifications;

/**
 * Smart sort chain used for list endpoints:
 * pinned DESC → sortOrder ASC → datePosted DESC.
 * Applied as an SQL ORDER BY for real DB-side pagination.
 */
@Service
public class ModelService {

    private static final Sort SMART_SORT = Sort.by(
            Sort.Order.desc("pinned"),
            Sort.Order.asc("sortOrder"),
            Sort.Order.desc("datePosted")
    );

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

    /**
     * Full filtered list — category + skill filters, smart-sorted.
     * Used by admin panel and non-paginated callers.
     */
    public List<ModelDto> getAllModels(String category, Long skillId) {
        Specification<Model> spec = buildSpecification(category, skillId);
        return modelRepository.findAll(spec).stream()
                .map(this::convertToDtoWithPresignedUrl)
                .collect(Collectors.toList());
    }

    /**
     * Paginated list — DB-side filter + sort, then slice via PageRequest.
     * Only fetches the requested page from the database.
     */
    public Page<ModelDto> getAllModels(int page, int size, String category, Long skillId) {
        Pageable pageable = PageRequest.of(page, size, SMART_SORT);
        Specification<Model> spec = buildSpecification(category, skillId);
        return modelRepository.findAll(spec, pageable)
                .map(this::convertToDtoWithPresignedUrl);
    }

    /** Convenience overload without filters. */
    public Page<ModelDto> getAllModels(int page, int size) {
        return getAllModels(page, size, null, null);
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
                .orElseThrow(() -> new ResourceNotFoundException("Model not found with id " + id));
    }

    public void deleteModel(Long id) {
        if (!modelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Model not found with id " + id);
        }
        modelRepository.deleteById(id);
    }

    private Specification<Model> buildSpecification(String category, Long skillId) {
        return ContentSpecifications.<Model>categoryIs(category, "category")
                .and(ContentSpecifications.<Model>skillIdIs(skillId));
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