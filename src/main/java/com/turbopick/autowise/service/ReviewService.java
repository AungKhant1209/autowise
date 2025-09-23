// src/main/java/com/turbopick/autowise/service/ReviewService.java
package com.turbopick.autowise.service;

import com.turbopick.autowise.dto.ReviewDto;
import com.turbopick.autowise.model.*;
import com.turbopick.autowise.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final CarRepository carRepo;
    private final UserAccountRepository userRepo;

    public ReviewService(ReviewRepository reviewRepo,
                         CarRepository carRepo,
                         UserAccountRepository userRepo) {
        this.reviewRepo = reviewRepo;
        this.carRepo = carRepo;
        this.userRepo = userRepo;
    }


    @Transactional(readOnly = true)
    public List<Review> listForCar(Long carId) {
        return reviewRepo.findByCarIdOrderByIdDesc(carId);
    }


    @Transactional(readOnly = true)
    public double averageForCar(Long carId) {
        Double avg = reviewRepo.avgRatingForCar(carId);
        return avg == null ? 0.0 : avg;
    }
    @Transactional(readOnly = true)
    public boolean hasUserReviewedCar(Long carId, Long userId) {
        return reviewRepo.existsByCar_IdAndUser_Id(carId, userId);
    }

    @Transactional
    public Review addReview(Long carId, Long userId, ReviewDto dto) {
        Car car = carRepo.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car not found: " + carId));

        UserAccount user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        if (reviewRepo.existsByCarIdAndUser_Id(carId, userId)) {
            throw new IllegalStateException("You already reviewed this car");
        }


        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review r = new Review();
        r.setCar(car);
        r.setUser(user);
        r.setRating(dto.getRating());
        r.setComment(dto.getComment());
        return reviewRepo.save(r);
    }
}