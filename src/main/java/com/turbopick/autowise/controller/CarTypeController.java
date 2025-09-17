package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.dto.CarTypeDto;
import com.turbopick.autowise.repository.CarTypeRepository;
import com.turbopick.autowise.service.CarTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CarTypeController {

    @Autowired
    private CarTypeService carTypeService;

    @Autowired
    private CarTypeRepository carTypeRepository;

    // --- LIST ---
    @GetMapping("/admin/carTypes")
    public String listCarTypes(Model model) {
        List<CarType> carTypes = carTypeService.findAllCarTypes();
        model.addAttribute("carTypes", carTypes);
        return "admin/carTypes";
    }

    // --- CREATE ---
    @GetMapping("/admin/carTypesCreate")
    public String create(Model model) {
        model.addAttribute("carTypeDto", new CarTypeDto());
        return "admin/carTypesCreate";
    }

    @PostMapping("/admin/carTypesCreate")
    public String createCarType(@Valid @ModelAttribute("carTypeDto") CarTypeDto carTypeDto,
                                BindingResult result) {

        CarType existing = carTypeRepository.findByTypeName(carTypeDto.getTypeName());
        if (existing != null) {
            result.addError(new FieldError("carTypeDto","typeName","Type name already exists"));
        }

        if (result.hasErrors()) {
            return "admin/carTypesCreate";
        }

        CarType carType = new CarType();
        carType.setTypeName(carTypeDto.getTypeName());
        carType.setDescription(carTypeDto.getDescription());
        carTypeRepository.save(carType);

        return "redirect:/admin/carTypes";
    }

    // --- EDIT ---
    @GetMapping("/admin/carTypesEdit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        CarType carType = carTypeService.findById(id);
        if (carType == null) return "redirect:/admin/carTypes";

        CarTypeDto dto = new CarTypeDto();
        dto.setTypeName(carType.getTypeName());
        dto.setDescription(carType.getDescription());

        model.addAttribute("carType", carType);   // for th:action id
        model.addAttribute("carTypeDto", dto);    // form-backing bean
        return "admin/carTypesEdit";
    }

    @PostMapping("/admin/carTypesEdit/{id}")
    public String editCarType(@PathVariable Long id,
                              @Valid @ModelAttribute("carTypeDto") CarTypeDto carTypeDto,
                              BindingResult result,
                              Model model) {
        CarType existing = carTypeService.findById(id);
        if (existing == null) return "redirect:/admin/carTypes";

        if (result.hasErrors()) {
            model.addAttribute("carType", existing);
            return "admin/carTypesEdit";
        }

        existing.setTypeName(carTypeDto.getTypeName());
        existing.setDescription(carTypeDto.getDescription());
        carTypeRepository.save(existing);
        return "redirect:/admin/carTypes";
    }

    // --- DELETE ---
    @GetMapping("/admin/carTypeDelete/{id}")
    public String deleteCarType(@PathVariable Long id, RedirectAttributes ra) {
        try {
            CarType ct = carTypeService.findById(id);
            if (ct == null) {
                ra.addFlashAttribute("alert", "Car type not found.");
                return "redirect:/admin/carTypes";
            }
            carTypeRepository.deleteById(id);
            ra.addFlashAttribute("notice", "Car type deleted.");
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            ra.addFlashAttribute("alert", "Cannot delete: this car type is in use.");
        }
        return "redirect:/admin/carTypes";
    }
}
