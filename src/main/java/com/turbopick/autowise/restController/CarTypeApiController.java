package com.turbopick.autowise.restController;

import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.service.CarTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/carTypes")
public class CarTypeApiController {
    @Autowired
    private CarTypeService carTypeService;

    @PostMapping
    public CarType createCarType(@RequestBody CarType carType) {
        return carTypeService.saveCarType(carType);
    }
    @GetMapping
    public List<CarType> getAllCarTypes() {
        return carTypeService.findAllCarTypes();
    }
    @GetMapping("/{id}")
    public Optional<CarType>getCarTypeById(@PathVariable int id) {
        return carTypeService.findCarTypeById(id);
    }
    @DeleteMapping("/{id}")
    public void deleteCarTypeById(@PathVariable int id) {
        carTypeService.deleteCarTypeById(id);
    }

}

