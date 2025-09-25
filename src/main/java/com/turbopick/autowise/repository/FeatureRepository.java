package com.turbopick.autowise.repository;


import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.model.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature,Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Feature> findByNameIgnoreCase(String name);


    @Query("""
           select distinct f.category
           from Feature f
           where f.category is not null and f.category <> ''
           order by f.category asc
           """)
    List<String> findDistinctCategories();
}
