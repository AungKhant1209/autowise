package com.turbopick.autowise.restController;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/api/cars")
//public class CarApiController {
//
//    @Autowired
//    private CarService carService;
//
//    @PostMapping
//    public Car saveCar(@RequestBody Car car) {
//        return carService.saveCar(car);
//    }
//
//    @GetMapping
//    public List<Car> getAllCars() {
//        return carService.getAllCars();
//    }
//
//    @GetMapping("/{id}")
//    public Optional<Car> getCarById(@PathVariable Long id) {
//        return carService.findCarById(id);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteCar(@PathVariable Long id) {
//        carService.deleteCarById(id);
//    }
//}
