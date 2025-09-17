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
import com.turbopick.autowise.service.CarTypeService;
import com.turbopick.autowise.service.FeatureService;
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
    private FeatureService featureService;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private CarService carService;

    @Autowired
    private CarTypeService carTypeService;

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
        return carTypeService.findAll();
    }

    @ModelAttribute("carBrands")
    public List<CarBrand> carBrands() {
        return carBrandRepository.findAll();
    }

    // --- LIST ---
    @GetMapping("/carList")
    public String cars(Model model) {
        List<Car> cars = carService.findAll();
        model.addAttribute("cars", cars);
        return "car-list";
    }

    @GetMapping("/cars")
    public String legacyCarsRedirect() {
        return "redirect:/admin/cars";
    }

    @GetMapping("/admin/cars")
    public String getCars(Model model) {
        List<Car> cars = carService.findAll();
        model.addAttribute("cars", cars);
        return "admin/cars";
    }

    // --- CREATE ---
    @GetMapping({"/carCreate", "/admin/carsCreate"})
    public String showCarForm(Model model) {
        model.addAttribute("carDto", new CarDto());
        model.addAttribute("allFeatures", featureService.findAllFeatures());
        return "admin/carCreate";
    }

    @PostMapping({"/carCreate", "/admin/carsCreate"})
    public String createCar(@Valid @ModelAttribute("carDto") CarDto carDto,
                            BindingResult result,
                            Model model) {

        // Unique name check (example)
        if (carService.findByName(carDto.getName()) != null) {
            result.rejectValue("name", "duplicate", "Car name already exists");
        }

        CarType type = null;
        String rawTypeId = carDto.getCarTypeId();
        if (rawTypeId == null || rawTypeId.isBlank()) {
            result.rejectValue("carTypeId", "required", "Car type is required");
        } else {
            try {
                Long typeId = Long.parseLong(rawTypeId.trim());
                type = carTypeService.findById(typeId);
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
            model.addAttribute("allFeatures", featureService.findAllFeatures());
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
            List<Feature> selected = featureService.findAllByIds(carDto.getFeatureIds());
            car.getFeatures().addAll(new HashSet<>(selected));
        }

        Car carSaved = carRepository.save(car);

        Car carSaved=carService.save(car);
        MultipartFile[] files=carDto.getFiles();
        long nonEmpty = Arrays.stream(files).filter(f -> !f.isEmpty()).count();


        try {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String ct = file.getContentType();
                if (ct == null || !ct.startsWith("image/")) {
                    return "redirect:/admin/imageUpload?carId=" + carSaved.getId();
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
        var carOpt = carService.findByIdWithFeatures(id);
        if (carOpt.isEmpty()) return "redirect:/carList";
        model.addAttribute("car", carOpt.get());
        return "listing-single";
    }

    // --- EDIT ---
    @GetMapping({"/editCar/{id}", "/admin/editCar/{id}"})
    public String editCar(@PathVariable Long id, Model model) {
        Car car = carService.findByIdOrNull(id);
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
        model.addAttribute("allFeatures", featureService.findAllFeatures());
        return "/admin/carEdit";
    }

    @PostMapping({"/editCar/{id}", "/admin/editCar/{id}"})
    public String updateCar(@PathVariable Long id,
                            @Valid @ModelAttribute("carDto") CarDto carDto,
                            BindingResult result,
                            Model model) {
        Car existing = carService.findByIdOrNull(id);
        if (existing == null) return "redirect:/admin/cars";

        CarType type = null;
        String rawTypeId = carDto.getCarTypeId();
        if (rawTypeId == null || rawTypeId.isBlank()) {
            result.rejectValue("carTypeId", "required", "Car type is required");
        } else {
            try {
                typeId = Long.parseLong(rawTypeId.trim());
            } catch (NumberFormatException e) {
                result.rejectValue("carTypeId", "invalid", "Invalid car type");
            }
            if (typeId != null) {
                type = carTypeService.findById(typeId);
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
            model.addAttribute("carId", id);
            model.addAttribute("allFeatures", featureService.findAllFeatures());
            return "/admin/carEdit";
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
        List<Long> ids = carDto.getFeatureIds();
        if (ids != null && !ids.isEmpty()) {
            List<Feature> selected = featureService.findAllByIds(ids);
            existing.getFeatures().addAll(new HashSet<>(selected));
        }

        carService.save(existing);
        return "redirect:/admin/cars";
    }

    // Delete
    @GetMapping("/carDelete/{id}")
    public String deleteCar(@PathVariable Long id) {
        carService.findById(id).ifPresent(car -> {
            // 1) Clear Many-to-Many to avoid join-table FK violations
            if (car.getFeatures() != null) {
                car.getFeatures().clear();
            }

        carService.deleteCarById(id); // clears join tables + hard deletes + flush

            // 3) If you have other children (e.g., carImages), clear them here too
            //    e.g., car.getImages().clear(); (and/or delete from image repo first)

            // Persist the detach so join/FK rows are gone before delete
            carService.save(car);

            // Now it's safe to delete the car row
            carService.delete(car);
        });
        return "redirect:/admin/cars";
    }


}
