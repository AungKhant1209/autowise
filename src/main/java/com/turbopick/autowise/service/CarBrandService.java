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


    public CarBrand save(CarBrand brand) {
        return carBrandRepository.save(brand);
    }



    public Optional<CarBrand> findById(Long id) {
        return carBrandRepository.findById(id);
    }



    public List<CarBrand> findAll() {
        return carBrandRepository.findAll();
    }

    public boolean existsById(Long id) {
        return carBrandRepository.existsById(id);
    }


    public boolean existsByBrandName(String brandName) {
        if (brandName == null) return false;
        String normalized = brandName.trim();
        if (normalized.isEmpty()) return false;
        return carBrandRepository.existsByBrandNameIgnoreCase(normalized);
    }

    public boolean existsByBrandNameExcludingId(Long brandId, String brandName) {
        if (brandName == null || brandId == null) return false;
        String normalized = brandName.trim();
        if (normalized.isEmpty()) return false;
        return carBrandRepository.existsByBrandNameIgnoreCaseAndBrandIdNot(normalized, brandId);
    }




    @Transactional
    public void delete(CarBrand brand) {
        if (brand == null || brand.getBrandId() == null) {
            throw new IllegalArgumentException("CarBrand (with id) is required for delete");
        }
        CarBrand managed = carBrandRepository.findById(brand.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("CarBrand not found: " + brand.getBrandId()));
        carBrandRepository.delete(managed);
    }


}