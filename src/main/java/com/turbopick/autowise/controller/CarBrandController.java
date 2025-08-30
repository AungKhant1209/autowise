package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.CarBrand;
import com.turbopick.autowise.repository.CarBrandRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Controller
public class CarBrandController {


    private final CarBrandRepository carBrandRepository;

    public CarBrandController(CarBrandRepository carBrandRepository) {
        this.carBrandRepository = carBrandRepository;
    }

    @GetMapping("/carBrands")
    public String list(Model model) {
        model.addAttribute("carBrands", carBrandRepository.findAll());
        return "carBrands";
    }

    @GetMapping("/carBrandsCreate")
    public String createForm(Model model) {
        model.addAttribute("carBrand", new CarBrand());
        return "carBrandCreate";
    }

    @PostMapping("/carBrandsCreate")
    public String create(@Valid @ModelAttribute("carBrand") CarBrand carBrand,
                         BindingResult result) {
        if (carBrand.getBrandName() == null || carBrand.getBrandName().isBlank()) {
            result.addError(new FieldError("carBrand", "brandName", "Brand name is required"));
        }
        if (!result.hasFieldErrors("brandName") &&
                carBrandRepository.existsByBrandName(carBrand.getBrandName())) {
            result.addError(new FieldError("carBrand", "brandName", "Brand name already exists"));
        }
        if (result.hasErrors()) {
            return "carBrandCreate";
        }
        carBrandRepository.save(carBrand);
        return "redirect:/carBrands";
    }

    @GetMapping("/carBrandsEdit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        CarBrand b = carBrandRepository.findById(id).orElse(null);
        if (b == null) return "redirect:/carBrands";
        model.addAttribute("carBrand", b);
        return "carBrandEdit";
    }

    @PostMapping("/carBrandsEdit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("carBrand") CarBrand carBrand,
                         BindingResult result) {
        if (!carBrandRepository.existsById(id)) return "redirect:/carBrands";

        if (carBrand.getBrandName() == null || carBrand.getBrandName().isBlank()) {
            result.addError(new FieldError("carBrand", "brandName", "Brand name is required"));
        }

        // NEW: duplicate name check (excluding this brand)
        if (!result.hasFieldErrors("brandName") &&
                carBrandRepository.existsByBrandNameAndBrandIdNot(carBrand.getBrandName(), id)) {
            result.addError(new FieldError("carBrand", "brandName", "Brand name already exists"));
        }

        if (result.hasErrors()) {
            return "carBrandEdit";
        }

        carBrand.setBrandId(id);
        carBrandRepository.save(carBrand);
        return "redirect:/carBrands";
    }

    @GetMapping("/carBrandsDelete/{id}")
    public String delete(@PathVariable Long id) {
        if (carBrandRepository.existsById(id)) {
            carBrandRepository.deleteById(id);
        }
        return "redirect:/carBrands";
    }
}
