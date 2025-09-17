package com.turbopick.autowise.controller;

import com.turbopick.autowise.dto.CarDto;
import org.springframework.transaction.annotation.Transactional;
import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.model.CarBrand;
import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.model.Feature;
import com.turbopick.autowise.repository.CarBrandRepository;
import com.turbopick.autowise.repository.CarRepository;
import com.turbopick.autowise.repository.CarTypeRepository;
import com.turbopick.autowise.repository.FeatureRepository;
import com.turbopick.autowise.service.CarService;
import com.turbopick.autowise.service.S3Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private CarBrandRepository carBrandRepository;

    @Autowired
    private S3Service s3Service;

    // --- expose car types & brands to views ---
    @ModelAttribute("carTypes")
    public List<CarType> carTypes() {
        return carTypeRepository.findAll();
    }

    @ModelAttribute("carBrands")
    public List<CarBrand> carBrands() {
        return carBrandRepository.findAll();
    }

    // --- LIST ---
    @GetMapping("/carList")
    public String cars(Model model) {
        List<Car> cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "car-list";
    }

    @GetMapping("/cars")
    public String legacyCarsRedirect() {
        return "redirect:/admin/cars";
    }

    @GetMapping("/admin/cars")
    public String getCars(Model model) {
        List<Car> cars = carService.getAllCars();
        model.addAttribute("cars", cars);
        return "admin/cars";
    }

    // --- CREATE ---
    @GetMapping({"/carCreate", "/admin/carsCreate"})
    public String showCarForm(Model model) {
        model.addAttribute("carDto", new CarDto());
        model.addAttribute("allFeatures", featureRepository.findAll());
        return "admin/carCreate";
    }

    @PostMapping({"/carCreate", "/admin/carsCreate"})
    public String createCar(@Valid @ModelAttribute("carDto") CarDto carDto,
                            BindingResult result,
                            Model model) {

        if (carRepository.findCarByName(carDto.getName()) != null) {
            result.rejectValue("name", "duplicate", "Car name already exists");
        }

        CarType type = null;
        String rawTypeId = carDto.getCarTypeId();
        if (rawTypeId == null || rawTypeId.isBlank()) {
            result.rejectValue("carTypeId", "required", "Car type is required");
        } else {
            try {
                Long typeId = Long.parseLong(rawTypeId.trim());
                type = carTypeRepository.findById(typeId).orElse(null);
                if (type == null) {
                    result.rejectValue("carTypeId", "invalid", "Invalid car type");
                }
            } catch (NumberFormatException e) {
                result.rejectValue("carTypeId", "invalid", "Invalid car type");
            }
        }

        CarBrand brand = null;
        Long brandId = carDto.getCarBrandId();
        if (brandId == null) {
            result.rejectValue("carBrandId", "required", "Car brand is required");
        } else {
            brand = carBrandRepository.findById(brandId).orElse(null);
            if (brand == null) {
                result.rejectValue("carBrandId", "invalid", "Invalid car brand");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("allFeatures", featureRepository.findAll());
            return "admin/carCreate";
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
        car.setCarType(type);
        car.setCarBrand(brand);

        if (carDto.getFeatureIds() != null && !carDto.getFeatureIds().isEmpty()) {
            List<Feature> selected = featureRepository.findAllById(carDto.getFeatureIds());
            car.getFeatures().addAll(new HashSet<>(selected));
        }

        Car carSaved = carRepository.save(car);

        MultipartFile[] files = carDto.getFiles();
        if (files != null) {
            try {
                for (MultipartFile file : files) {
                    if (file.isEmpty()) continue;
                    String ct = file.getContentType();
                    if (ct == null || !ct.startsWith("image/")) continue;
                    String url = s3Service.uploadFile(file);
                    if (url != null && !url.isBlank()) {
                        carSaved.getImageUrls().add(url);
                    }
                }
                carRepository.save(carSaved);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/admin/cars";
    }

    // --- DETAIL ---
    @GetMapping("/car-detail/{id}")
    public String carDetail(@PathVariable Long id, Model model) {
        var carOpt = carRepository.findByIdWithFeatures(id);
        if (carOpt.isEmpty()) return "redirect:/carList";
        model.addAttribute("car", carOpt.get());
        return "listing-single";
    }

    // --- EDIT ---
    @GetMapping({"/editCar/{id}", "/admin/editCar/{id}"})
    public String editCar(@PathVariable Long id, Model model) {
        Car car = carRepository.findCarById(id);
        if (car == null) return "redirect:/admin/cars";

        CarDto carDto = new CarDto();
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

        if (car.getCarType() != null) {
            carDto.setCarTypeId(String.valueOf(car.getCarType().getTypeId()));
        }
        if (car.getCarBrand() != null) {
            carDto.setCarBrandId(car.getCarBrand().getBrandId());
        }

        carDto.setFeatureIds(
                car.getFeatures().stream().map(Feature::getId).collect(Collectors.toList())
        );

        model.addAttribute("carId", id);
        model.addAttribute("carDto", carDto);
        model.addAttribute("allFeatures", featureRepository.findAll());
        return "/admin/carEdit";
    }

    @PostMapping({"/editCar/{id}", "/admin/editCar/{id}"})
    public String updateCar(@PathVariable Long id,
                            @Valid @ModelAttribute("carDto") CarDto carDto,
                            BindingResult result,
                            Model model) {
        Car existing = carRepository.findCarById(id);
        if (existing == null) return "redirect:/admin/cars";

        CarType type = null;
        String rawTypeId = carDto.getCarTypeId();
        if (rawTypeId == null || rawTypeId.isBlank()) {
            result.rejectValue("carTypeId", "required", "Car type is required");
        } else {
            try {
                Long typeId = Long.parseLong(rawTypeId.trim());
                type = carTypeRepository.findById(typeId).orElse(null);
                if (type == null) {
                    result.rejectValue("carTypeId", "invalid", "Invalid car type");
                }
            } catch (NumberFormatException e) {
                result.rejectValue("carTypeId", "invalid", "Invalid car type");
            }
        }

        CarBrand brand = null;
        Long brandId = carDto.getCarBrandId();
        if (brandId == null) {
            result.rejectValue("carBrandId", "required", "Car brand is required");
        } else {
            brand = carBrandRepository.findById(brandId).orElse(null);
            if (brand == null) {
                result.rejectValue("carBrandId", "invalid", "Invalid car brand");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("allFeatures", featureRepository.findAll());
            return "admin/carEdit";
        }

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
        existing.setCarType(type);
        existing.setCarBrand(brand);

        existing.getFeatures().clear();
        if (carDto.getFeatureIds() != null && !carDto.getFeatureIds().isEmpty()) {
            List<Feature> selected = featureRepository.findAllById(carDto.getFeatureIds());
            existing.getFeatures().addAll(selected);
        }

        carRepository.save(existing);
        return "redirect:/admin/cars";
    }

    // --- DELETE ---
    @PostMapping("/carDelete/{id}")
    @Transactional
    public String deleteCar(@PathVariable Long id, RedirectAttributes ra) {
        if (!carRepository.existsById(id)) {
            ra.addFlashAttribute("alert", "Car not found: " + id);
            return "redirect:/admin/cars";
        }

        carService.deleteCarById(id); // clears join tables + hard deletes + flush

        ra.addFlashAttribute("notice", "Deleted car #" + id);
        return "redirect:/admin/cars";
    }


}
