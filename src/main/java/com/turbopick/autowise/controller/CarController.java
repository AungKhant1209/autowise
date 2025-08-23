package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.model.CarDto;
import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.model.CarBrand;                              // <-- add
import com.turbopick.autowise.repository.CarRepository;
import com.turbopick.autowise.repository.CarTypeRepository;
import com.turbopick.autowise.repository.CarBrandRepository;               // <-- add
import com.turbopick.autowise.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CarController {
    @Autowired private CarService carService;
    @Autowired private CarRepository carRepository;
    @Autowired private CarTypeRepository carTypeRepository;
    @Autowired private CarBrandRepository carBrandRepository;              // <-- add

    @GetMapping("/carList")
    public String cars(Model model) {
        List<Car> cars = carService.getAllCars();
        model.addAttribute("cars", cars);
        return "car-list";
    }

    @GetMapping("/cars")
    public String getCars(Model model) {
        List<Car> cars = carService.getAllCars();
        model.addAttribute("cars", cars);
        return "cars";
    }

    // 3.1 GET /carCreate — add brands to the model
    @GetMapping("/carCreate")
    public String showCarForm(Model model) {
        model.addAttribute("carDto", new CarDto());
        model.addAttribute("carTypes", carTypeRepository.findAll());
        model.addAttribute("carBrands", carBrandRepository.findAll());     // <-- here
        return "carCreate";
    }

    // 3.2 POST /carCreate — load brand and set on entity
    @PostMapping("/carCreate")
    public String createCar(@Valid @ModelAttribute("carDto") CarDto carDto,
                            BindingResult result,
                            Model model) {

        // example duplicate name check
        if (carRepository.findCarByName(carDto.getName()) != null) {
            result.addError(new FieldError("carDto", "name", "Car name already exists"));
        }

        // Load CarType
        CarType type = null;
        if (carDto.getCarTypeId() != null) {
            type = carTypeRepository.findById(carDto.getCarTypeId()).orElse(null);
            if (type == null) {
                result.addError(new FieldError("carDto", "carTypeId", "Invalid car type"));
            }
        } else {
            result.addError(new FieldError("carDto", "carTypeId", "Car type is required"));
        }

        // Load CarBrand  <-- your snippet goes exactly here
        CarBrand brand = null;
        if (carDto.getBrandId() != null) {
            brand = carBrandRepository.findById(carDto.getBrandId()).orElse(null);
            if (brand == null) {
                result.addError(new FieldError("carDto", "brandId", "Invalid brand"));
            }
        } else {
            result.addError(new FieldError("carDto", "brandId", "Brand is required"));
        }

        // on errors, re-add lists for the form
        if (result.hasErrors()) {
            model.addAttribute("carTypes", carTypeRepository.findAll());
            model.addAttribute("carBrands", carBrandRepository.findAll()); // <-- re-add here
            return "carCreate";
        }

        // Build entity
        Car car = new Car();
        car.setName(carDto.getName());
        car.setYoutubeLink(carDto.getYoutubeLink());
        car.setPrice(carDto.getPrice());
        car.setFuelType(carDto.getFuelType());
        car.setProductionYear(carDto.getProductionYear());
        car.setEngineSize(carDto.getEngineSize());
        car.setSeat(carDto.getSeat() != null ? carDto.getSeat() : 0);
        car.setDoor(carDto.getDoor() != null ? carDto.getDoor() : 0);
        car.setWarranty(carDto.getWarranty());
        car.setTransmission(carDto.getTransmission());
        car.setDriveType(carDto.getDriveType());
        car.setColor(carDto.getColor());
        car.setDescription(carDto.getDescription());

        // Set relations
        car.setCarType(type);
        car.setCarBrand(brand);                                            // <-- set selected brand

        carRepository.save(car);
        return "redirect:/cars";
    }

    // 3.3 GET /editCar/{id} — pre-fill brand and send brand list
    @GetMapping("/editCar/{id}")
    public String editCar(@PathVariable int id, Model model) {
        Car car = carRepository.findCarById(id);
        if (car == null) return "redirect:/cars";

        CarDto carDto = new CarDto();
        // entity -> dto
        carDto.setName(car.getName());
        carDto.setYoutubeLink(car.getYoutubeLink());
        carDto.setPrice(car.getPrice());
        carDto.setFuelType(car.getFuelType());
        carDto.setProductionYear(car.getProductionYear());
        carDto.setEngineSize(car.getEngineSize());
        carDto.setSeat(car.getSeat());
        carDto.setDoor(car.getDoor());
        carDto.setWarranty(car.getWarranty());
        carDto.setTransmission(car.getTransmission());
        carDto.setDriveType(car.getDriveType());
        carDto.setColor(car.getColor());
        carDto.setDescription(car.getDescription());
        carDto.setCarTypeId(car.getCarType() != null ? car.getCarType().getTypeId() : null);
        carDto.setBrandId(car.getCarBrand() != null ? car.getCarBrand().getBrandId() : null); // <-- here

        model.addAttribute("carId", id);
        model.addAttribute("carDto", carDto);
        model.addAttribute("carTypes", carTypeRepository.findAll());
        model.addAttribute("carBrands", carBrandRepository.findAll());     // <-- here
        return "carEdit";
    }

    // 3.4 POST /editCar/{id} — load/set brand and re-add list on error
    @PostMapping("/editCar/{id}")
    public String updateCar(@PathVariable int id,
                            @Valid @ModelAttribute("carDto") CarDto carDto,
                            BindingResult result,
                            Model model) {
        Car existing = carRepository.findCarById(id);
        if (existing == null) return "redirect:/cars";

        // Load CarType
        CarType type = null;
        if (carDto.getCarTypeId() != null) {
            type = carTypeRepository.findById(carDto.getCarTypeId()).orElse(null);
            if (type == null) {
                result.addError(new FieldError("carDto", "carTypeId", "Invalid car type"));
            }
        } else {
            result.addError(new FieldError("carDto", "carTypeId", "Car type is required"));
        }

        // Load CarBrand  <-- your snippet goes exactly here
        CarBrand brand = null;
        if (carDto.getBrandId() != null) {
            brand = carBrandRepository.findById(carDto.getBrandId()).orElse(null);
            if (brand == null) {
                result.addError(new FieldError("carDto", "brandId", "Invalid brand"));
            }
        } else {
            result.addError(new FieldError("carDto", "brandId", "Brand is required"));
        }

        if (result.hasErrors()) {
            model.addAttribute("carId", id);
            model.addAttribute("carTypes", carTypeRepository.findAll());
            model.addAttribute("carBrands", carBrandRepository.findAll()); // <-- re-add here
            return "carEdit";
        }

        // dto -> entity
        existing.setName(carDto.getName());
        existing.setYoutubeLink(carDto.getYoutubeLink());
        existing.setPrice(carDto.getPrice());
        existing.setFuelType(carDto.getFuelType());
        existing.setProductionYear(carDto.getProductionYear());
        existing.setEngineSize(carDto.getEngineSize());
        existing.setSeat(carDto.getSeat() != null ? carDto.getSeat() : existing.getSeat());
        existing.setDoor(carDto.getDoor() != null ? carDto.getDoor() : existing.getDoor());
        existing.setWarranty(carDto.getWarranty());
        existing.setTransmission(carDto.getTransmission());
        existing.setDriveType(carDto.getDriveType());
        existing.setColor(carDto.getColor());
        existing.setDescription(carDto.getDescription());

        // Set relations
        existing.setCarType(type);
        existing.setCarBrand(brand);                                       // <-- set selected brand

        carRepository.save(existing);
        return "redirect:/cars";
    }

    @GetMapping("/carDelete/{id}")
    public String deleteCar(@PathVariable int id) {
        if (carRepository.existsById(id)) {
            carRepository.deleteById(id);
        }
        return "redirect:/cars";
    }
}
