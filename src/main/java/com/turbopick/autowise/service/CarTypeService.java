package com.turbopick.autowise.service;

import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.repository.CarTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class CarTypeService {

    private final CarTypeRepository carTypeRepository;

    public CarTypeService(CarTypeRepository carTypeRepository) {
        this.carTypeRepository = carTypeRepository;
    }

    /* ===== Create / Update ===== */

    /** Create or update a single CarType. */
    public CarType save(CarType carType) {
        return carTypeRepository.save(carType);
    }

    /** Bulk create / update. */
    public List<CarType> saveAll(Collection<CarType> carTypes) {
        return carTypeRepository.saveAll(carTypes);
    }

    /** Update by ID with fields from 'updated'. */
    public CarType update(Long id, CarType updated) {
        CarType existing = carTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CarType not found: " + id));
        existing.setTypeName(updated.getTypeName());
        existing.setDescription(updated.getDescription());
        return carTypeRepository.save(existing);
    }

    /* ===== Delete ===== */

    public void deleteById(Long id) {
        carTypeRepository.deleteById(id);
    }

    /* ===== Read ===== */

    public CarType findById(Long id) {
        return carTypeRepository.findById(id).orElse(null);
    }

    public List<CarType> findAll() {
        return carTypeRepository.findAll();
    }

    public boolean existsById(Long id) {
        return carTypeRepository.existsById(id);
    }

    /** Convenience finder if your repository defines it. */
    public Optional<CarType> findByTypeName(String typeName) {
        return Optional.ofNullable(carTypeRepository.findByTypeName(typeName));
    }
}




//@Service
//public class CarService {
//    @Autowired
//    private CarRepository carRepository;
//
//    public Car saveCar(Car car) {
//        return carRepository.save(car);
//    }
//
//    // Delete a car by ID
//    public void deleteCarById(int id) {
//        carRepository.deleteById(id);
//    }
//
//    // Find a car by ID
//    public Optional<Car> findCarById(int id) {
//        return carRepository.findById(id);
//    }
//
//    // Get all cars
//    public List<Car> getAllCars() {
//        return carRepository.findAll();
//    }
//}
