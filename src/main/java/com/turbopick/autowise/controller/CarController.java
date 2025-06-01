package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.service.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping
    public Car saveCar(@RequestBody Car car) {
        return carService.saveCar(car);
    }

    @GetMapping
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/{id}")
    public Optional<Car> getCarById(@PathVariable int id) {
        return carService.findCarById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable int id) {
        carService.deleteCarById(id);
    }
}