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

    // Create/Update
    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    // Delete a car by ID
    public void deleteCarById(int id) {
        carRepository.deleteById(id);
    }

    // Find a car by ID (Optional)
    public Optional<Car> findCarById(int id) {
        return carRepository.findById(id);
    }

    // Find a car by ID (direct return if you prefer)
    public Car findCarByIdDirect(int id) {
        return carRepository.findCarById(id);
    }

    // Find by name
    public Car findCarByName(String name) {
        return carRepository.findCarByName(name);
    }

    // Exists by id
    public boolean existsById(int id) {
        return carRepository.existsById(id);
    }

    // Get all cars
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }
}
