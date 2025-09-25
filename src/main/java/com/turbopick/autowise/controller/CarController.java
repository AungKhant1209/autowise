package com.turbopick.autowise.controller;

import com.turbopick.autowise.dto.CarDto;
import com.turbopick.autowise.dto.CarListViewDto;
import com.turbopick.autowise.dto.ReviewDto;
import com.turbopick.autowise.model.*;
import com.turbopick.autowise.service.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @ModelAttribute("carTypes")
    public List<CarType> carTypes() {
        return carTypeService.findAll();
    }

    @ModelAttribute("carBrands")
    public List<CarBrand> carBrands() {
        return carBrandService.findAll();
    }

    @GetMapping({"/carList", "/cars"})
    public String carList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String fuel,
            @RequestParam(required = false, name = "featureIds") List<Long> featureIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        CarListViewDto dto = carService.getCarsForListViewFiltered(
                name, minPrice, maxPrice, brandId, typeId, fuel, featureIds, pageable);

        // Keep selected filters
        model.addAttribute("selectedName", name);
        model.addAttribute("selectedMinPrice", minPrice);
        model.addAttribute("selectedMaxPrice", maxPrice);
        model.addAttribute("selectedBrandId", brandId);
        model.addAttribute("selectedTypeId", typeId);
        model.addAttribute("selectedFuel", fuel);
        model.addAttribute("selectedFeatureIds", featureIds);

        model.addAttribute("carListViewDto", dto);
        return "car-list";
    }

//    @GetMapping({"/carList", "/cars"})
//    public String carList(Model model) {
//        CarListViewDto carListViewDto=carService.getCarsForListView();
//        model.addAttribute("carListViewDto", carListViewDto);
//        return "car-list";
//    }

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

        String normalizedName = carService.normalizeName(carDto.getName());
        if (normalizedName == null || normalizedName.isEmpty()) {
            result.rejectValue("name", "required", "Car name is required");
        } else if (carService.nameExists(normalizedName)) {
            result.rejectValue("name", "duplicate", "Car name already exists");
        }

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

        if (result.hasErrors()) {
            model.addAttribute("carTypes", carTypeService.findAll());
            model.addAttribute("carBrands", carBrandService.findAll());
            model.addAttribute("allFeatures", featureService.findAllFeatures());
            return "admin/carCreate";
        }

        List<Feature> selected = (carDto.getFeatureIds() == null || carDto.getFeatureIds().isEmpty())
                ? java.util.Collections.emptyList()
                : featureService.findAllByIds(carDto.getFeatureIds());

        Car car = carService.buildCarForCreate(carDto, type, brand, selected);
        car.setName(normalizedName);
        Car saved = carService.save(car);

        MultipartFile[] files = carDto.getFiles();
        if (files != null && files.length > 0) {
            try {
                for (MultipartFile file : files) {
                    if (file.isEmpty()) continue;
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        return "redirect:/admin/imageUpload?carId=" + saved.getId();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/admin/cars";
    }

    @GetMapping("/car-detail/{id}")
    public String carDetail(@PathVariable Long id,
                            @AuthenticationPrincipal(expression = "username") String email,
                            Model model) {
        var carOpt = carService.findByIdWithFeatures(id);
        if (carOpt.isEmpty()) return "redirect:/carList";

        model.addAttribute("car", carOpt.get());
        model.addAttribute("reviews", reviewService.listForCar(id));
        model.addAttribute("avgRating", reviewService.averageForCar(id));
        model.addAttribute("reviewDto", new ReviewDto());
        model.addAttribute("loggedInUserEmail", email);
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
        if (email == null) return "redirect:/login";

        var user = userAccountService.findByEmailOrThrow(email);

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


        String normalizedName = carService.normalizeName(carDto.getName());
        if (normalizedName == null || normalizedName.isEmpty()) {
            result.rejectValue("name", "required", "Car name is required");
        } else if (carService.nameExistsExcludingId(id, normalizedName)) {
            result.rejectValue("name", "duplicate", "Car name already exists");
        }

        CarType type = null;
        String rawTypeId = carDto.getCarTypeId();
        if (rawTypeId == null || rawTypeId.trim().isEmpty()) {
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
            brand = carBrandService.findById(brandId).orElse(null);
            if (brand == null) {
                result.rejectValue("carBrandId", "invalid", "Invalid car brand");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("carId", id);
            model.addAttribute("carTypes", carTypeService.findAll());
            model.addAttribute("carBrands", carBrandService.findAll());
            model.addAttribute("allFeatures", featureService.findAllFeatures());
            return "admin/carEdit";
        }


        List<Feature> selected = (carDto.getFeatureIds() == null || carDto.getFeatureIds().isEmpty())
                ? java.util.Collections.emptyList()
                : featureService.findAllByIds(carDto.getFeatureIds());


        carDto.setName(normalizedName);
        carService.applyDtoToEntity(existing, carDto, type, brand, selected);
        carService.save(existing);

        return "redirect:/admin/cars";
    }
    @PostMapping("/admin/cars/{id}/delete")
    public String deleteCar(@PathVariable long id,
                            org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        boolean removed = carService.deleteByIdWithCleanup(id);
        ra.addFlashAttribute("msg", removed ? "Car deleted" : "Car not found");
        // Add a cache-buster to be 100% sure you see fresh list
        return "redirect:/admin/cars?ts=" + System.currentTimeMillis();
    }
    @GetMapping("/admin/featureCreate")
    public String featureCreateForm(Model model) {
        model.addAttribute("feature", new Feature());
        return "admin/featureCreate";
    }

    @PostMapping("/admin/featureCreate")
    public String featureCreateSubmit(@Valid @ModelAttribute("feature") Feature feature,
                                      BindingResult result,
                                      RedirectAttributes ra) {
        if (result.hasErrors()) return "admin/featureCreate";
        featureService.save(feature);
        ra.addFlashAttribute("msg", "Feature created.");
        return "redirect:/admin/featureList";
    }



}
