package com.turbopick.autowise.service;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    public Optional<Car> findCarById(Long id) {
        return carRepository.findById(id);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    /** Defensive delete: clear join/collection tables, then hard delete car, then flush. */
    @Transactional
    public void deleteCarById(Long id) {
        if (!carRepository.existsById(id)) return;

        // 1) clear dependents
        carRepository.deleteCarFeatures(id);
        carRepository.deleteCarImages(id);

        // 2) hard delete parent row
        carRepository.hardDeleteCar(id);

        // 3) flush immediately so next request sees the change
        carRepository.flush();
    }
}
