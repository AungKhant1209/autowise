package com.turbopick.autowise.repository;

import com.turbopick.autowise.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
    public Car findCarById(Long id);
    public Car findCarByName(String name);

    public boolean existsById(Integer id);
    // CarRepository.java
    @Query("select c from Car c left join fetch c.features where c.id = :id")
    Optional<Car> findByIdWithFeatures(@Param("id") Long id);



}

