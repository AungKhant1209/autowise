package com.turbopick.autowise.repository;


import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.model.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepository extends JpaRepository<Feature,Long> {
}
