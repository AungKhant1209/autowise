package com.turbopick.autowise.repository;

import com.turbopick.autowise.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Car findCarByName(String name);
    Car findCarById(Long id);

    @Query("select c from Car c left join fetch c.features where c.id = :id")
    Optional<Car> findByIdWithFeatures(@Param("id") Long id);

    // --- cleanup dependents ---
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM car_feature WHERE car_id = :id", nativeQuery = true)
    void deleteCarFeatures(@Param("id") long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM car_images WHERE car_id = :id", nativeQuery = true)
    void deleteCarImages(@Param("id") long id);

    // --- hard delete parent row ---
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM car WHERE id = :id", nativeQuery = true)
    void hardDeleteCar(@Param("id") long id);
}
