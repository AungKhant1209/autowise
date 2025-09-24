// src/main/java/com/turbopick/autowise/repository/ReviewRepository.java
package com.turbopick.autowise.repository;

import com.turbopick.autowise.model.Review;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // List all reviews for a car (newest first)
    List<Review> findByCarIdOrderByIdDesc(Long carId);
    boolean existsByCar_IdAndUser_Id(Long carId, Long userId);

    // Uniqueness: does this user already review this car?
    boolean existsByCarIdAndUser_Id(Long carId, Long userId); // NOTE: user is UserAccount

    // Average rating
    @Query("select avg(r.rating) from Review r where r.car.id = :carId")
    Double avgRatingForCar(@Param("carId") Long carId);
}