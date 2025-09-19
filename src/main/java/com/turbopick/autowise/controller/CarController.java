package com.turbopick.autowise.controller;

import com.turbopick.autowise.dto.CarDto;
import com.turbopick.autowise.dto.ReviewDto;
import com.turbopick.autowise.model.*;
import com.turbopick.autowise.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Controller
public class CarController {
    @Autowired
    private FeatureService featureService;

    @Autowired
    private CarService carService;

    @Autowired
    private CarTypeService carTypeService;

    @Autowired
    private CarBrandService carBrandService;

    @Autowired
    private S3Service s3Service;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserAccountService userAccountService;

    // --- expose car types & brands to views ---
    @ModelAttribute("carTypes")
    public List<CarType> carTypes() {
        return carTypeService.findAll();
    }

    @ModelAttribute("carBrands")
    public List<CarBrand> carBrands() {
        return carBrandService.findAll();
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

        // === Normalize name early (same string used everywhere) ===
        String normalizedName = carService.normalizeName(carDto.getName());
        if (normalizedName == null || normalizedName.isEmpty()) {
            result.rejectValue("name", "required", "Car name is required");
        } else if (carService.nameExists(normalizedName)) {
            result.rejectValue("name", "duplicate", "Car name already exists");
        }

        // === Car Type Validation ===
        CarType type = null;
        String rawTypeId = carDto.getCarTypeId();
        if (rawTypeId == null || rawTypeId.trim().isEmpty()) {
            result.rejectValue("carTypeId", "required", "Car type is required");
        } else {
            try {
                Long typeId = Long.parseLong(rawTypeId.trim());
                // carTypeService.findById should return null if not found (or adapt)
                type = carTypeService.findById(typeId);
                if (type == null) {
                    result.rejectValue("carTypeId", "invalid", "Invalid car type");
                }
            } catch (NumberFormatException e) {
                result.rejectValue("carTypeId", "invalid", "Invalid car type");
            }
        }

        // === Car Brand Validation ===
        CarBrand brand = null;
        Long brandId = carDto.getCarBrandId();
        if (brandId == null) {
            result.rejectValue("carBrandId", "required", "Car brand is required");
        } else {
            brand = carBrandService.findById(brandId).orElse(null);
            if (brand == null) {
                result.rejectValue("carBrandId", "invalid", "Invalid car brand");
            }
        }

        // === Return to form on validation errors (re-add ALL lists used by the view) ===
        if (result.hasErrors()) {
            model.addAttribute("carTypes", carTypeService.findAll());
            model.addAttribute("carBrands", carBrandService.findAll());
            model.addAttribute("allFeatures", featureService.findAllFeatures());
            return "admin/carCreate";
        }

        // === Resolve features ===
        List<Feature> selected = (carDto.getFeatureIds() == null || carDto.getFeatureIds().isEmpty())
                ? java.util.Collections.emptyList()
                : featureService.findAllByIds(carDto.getFeatureIds());

        // === Build & Save (uses normalized name) ===
        Car car = carService.buildCarForCreate(carDto, type, brand, selected);
        car.setName(normalizedName); // enforce normalized value
        Car saved = carService.save(car);

        // === Handle Image Uploads (left as in your code) ===
        MultipartFile[] files = carDto.getFiles();
        if (files != null && files.length > 0) {
            try {
                for (MultipartFile file : files) {
                    if (file.isEmpty()) continue;
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        return "redirect:/admin/imageUpload?carId=" + saved.getId();
                    }
                    // your actual upload/store logic here...
                }
            } catch (Exception e) {
                e.printStackTrace();
                // optionally: return an error page
            }
        }

        return "redirect:/admin/cars";
    }

    // --- DETAIL ---
    @GetMapping("/car-detail/{id}")
    public String carDetail(@PathVariable Long id,
                            @AuthenticationPrincipal(expression = "username") String email,
                            Model model) {
        var carOpt = carService.findByIdWithFeatures(id);
        if (carOpt.isEmpty()) return "redirect:/carList";

        model.addAttribute("car", carOpt.get());
        model.addAttribute("reviews", reviewService.listForCar(id));
        model.addAttribute("avgRating", reviewService.averageForCar(id));
        model.addAttribute("reviewDto", new ReviewDto());          // form-backing bean
        model.addAttribute("loggedInUserEmail", email);            // to show/hide the form
        model.addAttribute("userHasReviewed",
                (email != null) ? reviewService.hasUserReviewedCar(
                        id, userAccountService.findByEmailOrThrow(email).getId()) : false);

        return "listing-single";
    }

    @PostMapping("/cars/{id}/reviews")
    public String submitReview(@PathVariable Long id,
                               @Valid @ModelAttribute("reviewDto") ReviewDto reviewDto,
                               BindingResult result,
                               @AuthenticationPrincipal(expression = "username") String email,
                               Model model) {
        // must be logged in
        if (email == null) return "redirect:/login";

        var user = userAccountService.findByEmailOrThrow(email);

        // server-side validation fallback
        if (result.hasErrors()) {
            model.addAttribute("car", carService.findByIdOrNull(id));
            model.addAttribute("reviews", reviewService.listForCar(id));
            model.addAttribute("avgRating", reviewService.averageForCar(id));
            model.addAttribute("loggedInUserEmail", email);
            model.addAttribute("userHasReviewed", reviewService.hasUserReviewedCar(id, user.getId()));
            return "listing-single";
        }

        try {
            reviewService.addReview(id, user.getId(), reviewDto);
        } catch (IllegalStateException dup) { // already reviewed
            result.rejectValue("comment", "duplicate", "You already reviewed this car.");
            model.addAttribute("car", carService.findByIdOrNull(id));
            model.addAttribute("reviews", reviewService.listForCar(id));
            model.addAttribute("avgRating", reviewService.averageForCar(id));
            model.addAttribute("loggedInUserEmail", email);
            model.addAttribute("userHasReviewed", true);
            return "listing-single";
        }

        return "redirect:/car-detail/" + id;
    }



    // --- EDIT ---
    @GetMapping({"/editCar/{id}", "/admin/editCar/{id}"})
    public String editCar(@PathVariable Long id, Model model) {
        Car car = carService.findByIdOrNull(id);
        if (car == null) return "redirect:/admin/cars";

        CarDto carDto = carService.buildDtoForEdit(car);

        model.addAttribute("carId", id);
        model.addAttribute("carDto", carDto);
        model.addAttribute("carTypes", carTypeService.findAll());
        model.addAttribute("carBrands", carBrandService.findAll());
        model.addAttribute("allFeatures", featureService.findAllFeatures());
        return "admin/carEdit";
    }

    @PostMapping({"/editCar/{id}", "/admin/editCar/{id}"})
    public String updateCar(@PathVariable Long id,
                            @Valid @ModelAttribute("carDto") CarDto carDto,
                            BindingResult result,
                            Model model) {

        Car existing = carService.findByIdOrNull(id);
        if (existing == null) return "redirect:/admin/cars";

        // ===== Name (normalize + unique excluding current id) =====
        String normalizedName = carService.normalizeName(carDto.getName());
        if (normalizedName == null || normalizedName.isEmpty()) {
            result.rejectValue("name", "required", "Car name is required");
        } else if (carService.nameExistsExcludingId(id, normalizedName)) {
            result.rejectValue("name", "duplicate", "Car name already exists");
        }

        // ===== Car Type Validation =====
        CarType type = null;
        String rawTypeId = carDto.getCarTypeId();
        if (rawTypeId == null || rawTypeId.trim().isEmpty()) {
            result.rejectValue("carTypeId", "required", "Car type is required");
        } else {
            try {
                Long typeId = Long.parseLong(rawTypeId.trim());
                type = carTypeService.findById(typeId); // return null if not found
                if (type == null) {
                    result.rejectValue("carTypeId", "invalid", "Invalid car type");
                }
            } catch (NumberFormatException e) {
                result.rejectValue("carTypeId", "invalid", "Invalid car type");
            }
        }

        // ===== Car Brand Validation =====
        CarBrand brand = null;
        Long brandId = carDto.getCarBrandId();
        if (brandId == null) {
            result.rejectValue("carBrandId", "required", "Car brand is required");
        } else {
            brand = carBrandService.findById(brandId).orElse(null);
            if (brand == null) {
                result.rejectValue("carBrandId", "invalid", "Invalid car brand");
            }
        }

        // ===== On errors: re-add all lists used by the view =====
        if (result.hasErrors()) {
            model.addAttribute("carId", id);
            model.addAttribute("carTypes", carTypeService.findAll());
            model.addAttribute("carBrands", carBrandService.findAll());
            model.addAttribute("allFeatures", featureService.findAllFeatures());
            return "admin/carEdit";
        }

        // ===== Resolve features =====
        List<Feature> selected = (carDto.getFeatureIds() == null || carDto.getFeatureIds().isEmpty())
                ? java.util.Collections.emptyList()
                : featureService.findAllByIds(carDto.getFeatureIds());

        // ===== Apply DTO via service + save =====
        carDto.setName(normalizedName); // ensure normalized value goes into entity
        carService.applyDtoToEntity(existing, carDto, type, brand, selected);
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

        carService.deleteById(id); // clears join tables + hard deletes + flush

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
