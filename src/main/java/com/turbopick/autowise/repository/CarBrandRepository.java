package com.turbopick.autowise.repository;

import com.turbopick.autowise.model.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarBrandRepository extends JpaRepository<CarBrand, Long> {
    boolean existsByBrandName(String brandName);

    // Use this when updating to ensure the new name isn't already used by another brand
    boolean existsByBrandNameAndBrandIdNot(String brandName, Long brandId);
}
