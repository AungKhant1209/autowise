package com.turbopick.autowise.service;

import com.turbopick.autowise.model.CarBrand;
import com.turbopick.autowise.repository.CarBrandRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class CarBrandService {

    private final CarBrandRepository carBrandRepository;

    public CarBrandService(CarBrandRepository carBrandRepository) {
        this.carBrandRepository = carBrandRepository;
    }

    /* ========= Create / Update ========= */
    public CarBrand save(CarBrand brand) {
        return carBrandRepository.save(brand);
    }

    public List<CarBrand> saveAll(Collection<CarBrand> brands) {
        return carBrandRepository.saveAll(brands);
    }

    /* ========= Read ========= */
    public Optional<CarBrand> findById(Long id) {
        return carBrandRepository.findById(id);
    }

    public CarBrand findByIdOrNull(Long id) {
        return carBrandRepository.findById(id).orElse(null);
    }

    public CarBrand findByIdOrThrow(Long id) {
        return carBrandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CarBrand not found: " + id));
    }

    public List<CarBrand> findAll() {
        return carBrandRepository.findAll();
    }

    public boolean existsById(Long id) {
        return carBrandRepository.existsById(id);
    }

    /** Case-insensitive existence check (for create). */
    public boolean existsByBrandName(String brandName) {
        if (brandName == null) return false;
        String normalized = brandName.trim();
        if (normalized.isEmpty()) return false;
        return carBrandRepository.existsByBrandNameIgnoreCase(normalized);
    }

    /** Case-insensitive check excluding a specific id (for edit). */
    public boolean existsByBrandNameExcludingId(Long brandId, String brandName) {
        if (brandName == null || brandId == null) return false;
        String normalized = brandName.trim();
        if (normalized.isEmpty()) return false;
        return carBrandRepository.existsByBrandNameIgnoreCaseAndBrandIdNot(normalized, brandId);
    }

    /* ========= Delete ========= */
    @Transactional
    public void deleteById(Long id) {
        carBrandRepository.deleteById(id);
    }

    /** Delete using an entity instance (reattach to ensure lifecycle callbacks). */
    @Transactional
    public void delete(CarBrand brand) {
        if (brand == null || brand.getBrandId() == null) {
            throw new IllegalArgumentException("CarBrand (with id) is required for delete");
        }
        CarBrand managed = carBrandRepository.findById(brand.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("CarBrand not found: " + brand.getBrandId()));
        carBrandRepository.delete(managed);
    }

    /** Fast path when you only have the id (skips a SELECT). */
    @Transactional
    public void deleteByIdFast(Long id) {
        carBrandRepository.delete(carBrandRepository.getReferenceById(id));
    }
}