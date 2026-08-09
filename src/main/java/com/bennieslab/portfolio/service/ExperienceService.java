package com.bennieslab.portfolio.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bennieslab.portfolio.model.Experience;
import com.bennieslab.portfolio.repository.ExperienceRepository;
import com.bennieslab.portfolio.exception.ResourceNotFoundException;

@Service
public class ExperienceService {
    
    @Autowired
    private ExperienceRepository experienceRepository;

    public Optional<Experience> getExperienceById(Long id) {
        return experienceRepository.findById(id);
    }

    public List<Experience> getAllExperience() {
        return experienceRepository.findAll();
    }

    public Experience addExperience(Experience experience) {
        return experienceRepository.save(experience);
    }

    public void removeExperience(Long id) {
        if (!experienceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Experience not found with id " + id);
        }
        experienceRepository.deleteById(id);
    }
}
