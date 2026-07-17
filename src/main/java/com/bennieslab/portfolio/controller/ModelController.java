package com.bennieslab.portfolio.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bennieslab.portfolio.service.ModelService;
import com.bennieslab.portfolio.dto.ModelDto;
import com.bennieslab.portfolio.dto.ModelUpdateRequest;

@CrossOrigin(origins = "https://bennieslab.github.io")
@RestController
@RequestMapping("/models")
public class ModelController {

    private final ModelService modelService;

    @Autowired
    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping("/{id}")
    public Optional<ModelDto> getModelById(@PathVariable Long id) {
        return modelService.getModelById(id);
    }

    /**
     * If ?page and ?size are both supplied, returns a Spring Page<ModelDto> sorted
     * by the smart sort chain. Otherwise returns the full List<ModelDto>.
     */
    @GetMapping
    public ResponseEntity<?> getModels(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long skillId) {
        if (page != null && size != null) {
            Page<ModelDto> result = modelService.getAllModels(page, size, category, skillId);
            return ResponseEntity.ok(result);
        }
        List<ModelDto> result = modelService.getAllModels(category, skillId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/categories")
    public List<String> getAllCategories() {
        return modelService.getAllCategories();
    }

    @PostMapping
    public ModelDto createModel(@RequestBody ModelUpdateRequest model) {
        return modelService.addModel(model);
    }

    @PutMapping("/{id}")
    public ModelDto updateModel(@PathVariable Long id, @RequestBody ModelUpdateRequest updatedModel) {
        return modelService.updateModel(id, updatedModel);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        modelService.deleteModel(id);
    }
}