package com.turbopick.autowise.repository;

import com.turbopick.autowise.model.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarBrandRepository extends JpaRepository<CarBrand, Long> {

    // Case-insensitive lookups by brandName
    Optional<CarBrand> findByBrandNameIgnoreCase(String brandName);

    boolean existsByBrandNameIgnoreCase(String brandName);

    // For edits: same name exists on a DIFFERENT id
    boolean existsByBrandNameIgnoreCaseAndBrandIdNot(String brandName, Long brandId);
}