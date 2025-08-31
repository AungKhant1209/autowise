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

    @GetMapping("/carTypes")
    public String carTypes(Model model) {
        List<CarType>carTypes=carTypeService.findAllCarTypes();
        model.addAttribute("carTypes",carTypes);
        System.out.println("CarTypeController.carList"+ carTypes.size());
        return "carTypes";
    }

    @GetMapping("/carTypesCreate")
    public String create(Model model) {
        CarTypeDto carTypeDto=new CarTypeDto();
        model.addAttribute("carTypeDto",carTypeDto);
        return "carTypesCreate";
    }
    @PostMapping("/carTypesCreate")
    public String createCarType(@Valid @ModelAttribute("carTypeDto") CarTypeDto carTypeDto,
                                BindingResult result) {

        // unique name check
        CarType existing = carTypeRepository.findByTypeName(carTypeDto.getTypeName());
        if (existing != null) {
            result.addError(new FieldError("carTypeDto","typeName","Type name already exists"));
        }

        if (result.hasErrors()) {
            return "carTypesCreate";
        }

        CarType carType = new CarType();
        carType.setTypeName(carTypeDto.getTypeName());
        carType.setDescription(carTypeDto.getDescription());
        carTypeRepository.save(carType);

        return "redirect:/carTypes";
    }
    @GetMapping("/carTypesEdit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        CarType carType = carTypeService.findById(id);
        if (carType == null) return "redirect:/carTypes";

        CarTypeDto dto = new CarTypeDto();
        dto.setTypeName(carType.getTypeName());
        dto.setDescription(carType.getDescription());

        model.addAttribute("carType", carType);   // used by th:action for {id}
        model.addAttribute("carTypeDto", dto);    // form-backing bean
        return "carTypesEdit";
    }

    @PostMapping("/carTypesEdit/{id}")
    public String editCarType(@PathVariable Long id,
                              @Valid @ModelAttribute("carTypeDto") CarTypeDto carTypeDto,
                              BindingResult result,
                              Model model) {
        CarType existing = carTypeService.findById(id);
        if (existing == null) return "redirect:/carTypes";

        if (result.hasErrors()) {
            model.addAttribute("carType", existing); // keep id for th:action
            return "carTypesEdit";
        }

        existing.setTypeName(carTypeDto.getTypeName());
        existing.setDescription(carTypeDto.getDescription());
        carTypeRepository.save(existing);
        return "redirect:/carTypes";
    }
    @GetMapping("/carTypeDelete/{id}")
    public String deleteCarType(@PathVariable Long id,
                                RedirectAttributes ra) {
        try {
            CarType ct = carTypeService.findById(id);
            if (ct == null) {
                ra.addFlashAttribute("alert", "Car type not found.");
                return "redirect:/carTypes";
            }
            carTypeRepository.deleteById(id);
            ra.addFlashAttribute("notice", "Car type deleted.");
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            ra.addFlashAttribute("alert", "Cannot delete: this car type is in use.");
        }
        return "redirect:/carTypes";
    }



}
