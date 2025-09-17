package com.turbopick.autowise.service;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.repository.CarRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    /* ===== Create / Update ===== */

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public List<Car> saveAll(List<Car> cars) {
        return carRepository.saveAll(cars);
    }

    /* ===== Read ===== */

    /** Preferred: handle not-found at the call site. */
    public Optional<Car> findById(Long id) {
        return carRepository.findById(id);
    }

    /** Convenience: returns null if not found. */
    public Car findByIdOrNull(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    /** Convenience: throws if not found. */
    public Car findByIdOrThrow(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found: " + id));
    }

    public Optional<Car> findByName(String name) {
        return Optional.ofNullable(carRepository.findCarByName(name));
    }

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    public boolean existsById(Long id) {
        return carRepository.existsById(id);
    }

    /* ===== Delete ===== */

    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    /* ===== Compatibility helper (if you still call repository.findCarById) ===== */
    public Car findCarByIdCompat(Long id) {
        // Prefer using findById / findByIdOrNull elsewhere
        return carRepository.findById(id).orElse(null);
    }
    @Transactional(readOnly = true)
    public Optional<Car> findByIdWithFeatures(Long id) {
        // If you used EntityGraph on findById:
        // return carRepository.findById(id);

        // If you created a dedicated repo method:
        return carRepository.findByIdWithFeatures(id);
    }

    /** Convenience variant that returns null if not found. */
    @Transactional(readOnly = true)
    public Car findByIdWithFeaturesOrNull(Long id) {
        return findByIdWithFeatures(id).orElse(null);
    }
    @Transactional
    public void delete(Car car) {
        if (car == null || car.getId() == null) {
            throw new IllegalArgumentException("Car (with id) is required for delete");
        }
        carRepository.delete(
                carRepository.findById(car.getId()).orElseThrow(() ->
                        new IllegalArgumentException("Car not found: " + car.getId()))
        );
    }
}
