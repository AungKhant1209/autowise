package com.turbopick.autowise.service;

import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.repository.CarTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarTypeService {
    @Autowired
    private CarTypeRepository carTypeRepository;
    public List<CarType> findAllCarTypes() {
        return carTypeRepository.findAll();
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
