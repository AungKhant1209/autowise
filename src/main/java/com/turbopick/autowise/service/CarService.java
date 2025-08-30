package com.turbopick.autowise.service;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    // Delete a car by ID
    public void deleteCarById(Long id) {
        carRepository.deleteById(id);
    }

    // Find a car by ID
    public Optional<Car> findCarById(Long id) {
        return carRepository.findById(id);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }
}
