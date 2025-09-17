package com.turbopick.autowise.service;

import com.turbopick.autowise.model.Feature;

import com.turbopick.autowise.repository.FeatureRepository;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
@Service
public class FeatureService {

    private final FeatureRepository featureRepository;

    public FeatureService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    // Create / Update a single feature
    public Feature saveFeature(Feature feature) {
        return featureRepository.save(feature);
    }

    // Create / Update many features
    public List<Feature> saveAllFeatures(Collection<Feature> features) {
        return featureRepository.saveAll(features);
    }

    // Delete
    public void deleteFeatureById(Long id) {
        featureRepository.deleteById(id);
    }

    // Lookups
    public Optional<Feature> findFeatureById(Long id) {
        return featureRepository.findById(id);
    }

    public List<Feature> findAllFeatures() {
        return featureRepository.findAll();
    }

    public List<Feature> findAllByIds(List<Long> ids) {
        return featureRepository.findAllById(ids);
    }

    public boolean existsById(Long id) {
        return featureRepository.existsById(id);
    }
}