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



    public List<Feature> findAllFeatures() {
        return featureRepository.findAll();
    }

    public List<Feature> findAllByIds(List<Long> ids) {
        return featureRepository.findAllById(ids);
    }

}