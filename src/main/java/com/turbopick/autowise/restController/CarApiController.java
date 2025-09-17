package com.turbopick.autowise.restController;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarApiController {

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
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return carService.findCarById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCar(@PathVariable Long id) {
        if (carService.findCarById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            carService.deleteCarById(id);
            return ResponseEntity.ok("Car deleted successfully with id " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to delete car with id " + id + " : " + e.getMessage());
        }
    }
}
