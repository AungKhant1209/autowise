package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.*;
import com.turbopick.autowise.repository.CarRepository;
import com.turbopick.autowise.repository.CarTypeRepository;
import com.turbopick.autowise.repository.FeatureRepository;
import com.turbopick.autowise.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@Controller
public class CarController {
    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private CarService carService;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CarTypeRepository carTypeRepository;
    @ModelAttribute("carTypes")
    public java.util.List<CarType> carTypes() {
        return carTypeRepository.findAll();
    }


    @GetMapping("/carList")
    public String cars(Model model) {
        List<Car> cars = carService.getAllCars();
        model.addAttribute("cars", cars);
        System.out.println("CarController.carList"+ cars.size());
        return "car-list";
    }
    @GetMapping({"/cars"})
    public String getCars(Model model) {
        List<Car>  cars = carService.getAllCars();
        model.addAttribute("cars", cars);
        return "cars";
    }
    @GetMapping("/carCreate")
    public String showCarForm(Model model) {
        model.addAttribute("carDto", new CarDto()); // empty DTO for form binding
        model.addAttribute("carTypes", carTypeRepository.findAll());
        model.addAttribute("allFeatures", featureRepository.findAll());
        return "carCreate";
    }
    @PostMapping("carCreate")
    public String createCar(@Valid @ModelAttribute("carDto") CarDto carDto, BindingResult result,Model model){
        if (carRepository.findCarByName(carDto.getName()) != null ){
            FieldError error = new FieldError("carDto", "name", "Car name already exists");
            result.addError(error);
        }
        CarType type = carTypeRepository.findById(Long.valueOf(carDto.getCarTypeId())).orElse(null);
        if (type == null) {
            result.addError(new FieldError("carDto", "carTypeId", "Invalid car type"));
            model.addAttribute("carTypes", carTypeRepository.findAll());
            return "carCreate";
        }
        if (result.hasErrors()) {
            return "carCreate";
        }
        Car car = new Car();
        car.setName(carDto.getName());
        car.setYoutubeLink(carDto.getYoutubeLink());
        car.setPrice(carDto.getPrice());
        car.setFuelType(carDto.getFuelType());
        car.setProductionYear(carDto.getProductionYear());
        car.setEngineSize(carDto.getEngineSize());
        car.setSeat(carDto.getSeat());
        car.setDoor(carDto.getDoor());
        car.setWarranty(carDto.getWarranty());
        car.setTransmission(carDto.getTransmission());
        car.setDriveType(carDto.getDriveType());
        car.setColor(carDto.getColor());
        car.setDescription(carDto.getDescription());

        List<Long> ids = carDto.getFeatureIds();

        if (ids != null && !ids.isEmpty()) {
            List<Feature> selected = featureRepository.findAllById(ids);
            car.getFeatures().clear();
            car.getFeatures().addAll(new HashSet<>(selected));
        }
        carRepository.save(car);
        return "redirect:/cars";

    }
    @GetMapping("/editCar/{id}")
    public String editCar(@PathVariable int id, Model model) {
        Car car = carRepository.findCarById(id);

        if (car == null) return "redirect:/cars";

        System.out.println("this is car type::"+ car.getCarType().getTypeName());

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

        model.addAttribute("carId", id);     // <-- REQUIRED
        model.addAttribute("carDto", carDto);

        return "carEdit";
    }

    @PostMapping("/editCar/{id}")
    public String updateCar(@PathVariable int id,
                            @Valid @ModelAttribute("carDto") CarDto carDto,
                            BindingResult result,
                            Model model) {
        Car existing = carRepository.findCarById(id);
        if (existing == null) return "redirect:/cars";

        if (result.hasErrors()) {
            model.addAttribute("carId", id);  // <-- keep action URL valid on error
            return "carEdit";
        }

        // dto -> entity
        existing.setName(carDto.getName());
        existing.setYoutubeLink(carDto.getYoutubeLink());
        existing.setPrice(carDto.getPrice());
        existing.setFuelType(carDto.getFuelType());
        existing.setProductionYear(carDto.getProductionYear());
        existing.setEngineSize(carDto.getEngineSize());
        existing.setSeat(carDto.getSeat());
        existing.setDoor(carDto.getDoor());
        existing.setWarranty(carDto.getWarranty());
        existing.setTransmission(carDto.getTransmission());
        existing.setDriveType(carDto.getDriveType());
        existing.setColor(carDto.getColor());
        existing.setDescription(carDto.getDescription());
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