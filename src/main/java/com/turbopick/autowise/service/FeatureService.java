package com.turbopick.autowise.service;

import com.turbopick.autowise.model.Feature;

import com.turbopick.autowise.repository.FeatureRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;

    public FeatureService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }



    public List<Feature> findAllFeatures() {
        return featureRepository.findAll();
    }

    public List<Feature> findAllByIds(List<Long> ids) {
        return featureRepository.findAllById(ids);
    }
    @Transactional
    public Feature save(Feature f) {
        if (f == null) throw new IllegalArgumentException("Feature is required");

        // normalize
        String name = f.getName() != null ? f.getName().trim() : "";
        if (name.isEmpty()) throw new IllegalArgumentException("Feature name is required");
        f.setName(name);
        if (f.getCategory() != null) f.setCategory(f.getCategory().trim());
        if (f.getDescription() != null) f.setDescription(f.getDescription().trim());

        // uniqueness by name (case-insensitive)
        if (f.getId() == null) {
            // creating
            if (featureRepository.existsByNameIgnoreCase(name)) {
                throw new IllegalStateException("Feature already exists: " + name);
            }
        } else {
            // updating: ensure no collision with another record
            featureRepository.findByNameIgnoreCase(name)
                    .filter(existing -> !existing.getId().equals(f.getId()))
                    .ifPresent(existing -> {
                        throw new IllegalStateException("Another feature already uses this name: " + name);
                    });
        }

        return featureRepository.save(f);
    }

    @Transactional(readOnly = true)
    public List<Feature> findAll() {
        return featureRepository.findAll(Sort.by("name").ascending());
    }

    @Transactional(readOnly = true)
    public Feature findByIdOrThrow(Long id) {
        return featureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feature not found: " + id));
    }

    @Transactional
    public void deleteById(Long id) {
        featureRepository.deleteById(id);
    }


}