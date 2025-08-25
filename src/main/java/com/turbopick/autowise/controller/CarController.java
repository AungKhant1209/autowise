package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.*;
import com.turbopick.autowise.repository.CarBrandRepository;
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
import java.util.stream.Collectors;

@Controller
public class CarController {

    @Autowired private CarService carService;
    @Autowired private CarRepository carRepository;
    @Autowired private CarTypeRepository carTypeRepository;
    @Autowired private CarBrandRepository carBrandRepository;
    @Autowired private FeatureRepository featureRepository;

    @ModelAttribute("carTypes")
    public List<CarType> carTypes() {
        return carTypeRepository.findAll();
    }

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
        model.addAttribute("carBrands", carBrandRepository.findAll());
        model.addAttribute("allFeatures", featureRepository.findAll());
        return "carCreate";
    }

    @PostMapping("/carCreate")
    public String createCar(@Valid @ModelAttribute("carDto") CarDto carDto,
                            BindingResult result,
                            Model model) {

        CarBrand brand = null;
        if (carDto.getBrandId() != null) {
            brand = carBrandRepository.findById(carDto.getBrandId()).orElse(null);
            if (brand == null) {
                result.addError(new FieldError("carDto", "brandId", "Invalid brand"));
            }
        } else {
            result.addError(new FieldError("carDto", "brandId", "Brand is required"));
        }

        if (carRepository.findCarByName(carDto.getName()) != null) {
            result.rejectValue("name", "duplicate", "Car name already exists");
        }

        // parse & validate type
        CarType type = null;
        Long rawTypeId = carDto.getCarTypeId();
        if (rawTypeId == null) {
            result.rejectValue("carTypeId", "required", "Car type is required");
        }
        // (no else in your original code — leaving as-is; 'type' stays null)

        if (result.hasErrors()) {
            model.addAttribute("carTypes", carTypeRepository.findAll());
            model.addAttribute("allFeatures", featureRepository.findAll());
            return "carCreate";
        }

        Car car = new Car();
        // map scalars...
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

        car.setCarBrand(brand); // set selected brand
        car.setCarType(type);   // note: type remains null with current logic

        // features
        if (carDto.getFeatureIds() != null && !carDto.getFeatureIds().isEmpty()) {
            List<Feature> selected = featureRepository.findAllById(carDto.getFeatureIds());
            car.getFeatures().addAll(new HashSet<>(selected));
        }

        carRepository.save(car);
        return "redirect:/cars";
    }

    @GetMapping("/editCar/{id}")
    public String editCar(@PathVariable Long id, Model model) {
        Car car = carRepository.findCarById(id);
        if (car == null) return "redirect:/cars";

        CarDto carDto = new CarDto();
        // map scalars...
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
        carDto.setBrandId(car.getCarBrand() != null ? car.getCarBrand().getBrandId() : null);

        // preselect features
        carDto.setFeatureIds(
                car.getFeatures().stream().map(Feature::getId).collect(Collectors.toList())
        );

        model.addAttribute("carId", id);
        model.addAttribute("carDto", carDto);
        model.addAttribute("carTypes", carTypeRepository.findAll());
        model.addAttribute("allFeatures", featureRepository.findAll());
        model.addAttribute("carBrands", carBrandRepository.findAll());
        return "carEdit";
    }

    // 3.4 POST /editCar/{id} — load/set brand and re-add list on error
    @PostMapping("/editCar/{id}")
    public String updateCar(@PathVariable Long id,
                            @Valid @ModelAttribute("carDto") CarDto carDto,
                            BindingResult result,
                            Model model) {

        Car existing = carRepository.findCarById(id);
        if (existing == null) return "redirect:/cars";

        CarBrand brand = null;
        if (carDto.getBrandId() != null) {
            brand = carBrandRepository.findById(carDto.getBrandId()).orElse(null);
            if (brand == null) {
                result.addError(new FieldError("carDto", "brandId", "Invalid brand"));
            }
        } else {
            result.addError(new FieldError("carDto", "brandId", "Brand is required"));
        }

        // parse and validate carTypeId
        CarType type = null;
        Long rawTypeId = carDto.getCarTypeId();
        if (rawTypeId == null) {
            result.rejectValue("carTypeId", "required", "Car type is required");
        }
        // (no else in your original code — leaving as-is; 'type' stays null)

        if (result.hasErrors()) {
            model.addAttribute("carId", id);
            model.addAttribute("carTypes", carTypeRepository.findAll());
            model.addAttribute("carBrands", carBrandRepository.findAll());
            model.addAttribute("allFeatures", featureRepository.findAll());
            return "carEdit";
        }

        // map scalars
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

        existing.setCarBrand(brand);
        existing.setCarType(type); // note: type remains null with current logic

        // replace features (handles "all unchecked")
        existing.getFeatures().clear();
        List<Long> ids = carDto.getFeatureIds();
        if (ids != null && !ids.isEmpty()) {
            List<Feature> selected = featureRepository.findAllById(ids);
            existing.getFeatures().addAll(new HashSet<>(selected));
        }

        carRepository.save(existing);
        return "redirect:/cars";
    }

    @GetMapping("/carDelete/{id}")
    public String deleteCar(@PathVariable Long id) {
        if (carRepository.existsById(id)) {
            carRepository.deleteById(id);
        }
        return "redirect:/cars";
    }
}