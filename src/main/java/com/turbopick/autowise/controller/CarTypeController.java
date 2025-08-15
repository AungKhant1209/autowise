package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.model.CarTypeDto;
import com.turbopick.autowise.service.CarTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CarTypeController {
    @Autowired
    private CarTypeService carTypeService;

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
    public String createCarType(
            @Valid @ModelAttribute("carTypeDto")CarTypeDto carTypeDto, BindingResult result){
//        if (carTypeRespository.findByBrand(carTypeDto.getBrand())!=null) {
//            FieldError error=new FieldError("carTypeDto","brand",carTypeDto.getBrand());
//            result.addError(error);
//        }
//        if(result.hasErrors()) {
//            return "carTypesCreate";
//        }
//        CarType carType=new CarType();
//        carType.setName(carTypeDto.getName());
//        carType.setBrand(carTypeDto.getBrand());
//        carType.setCode(carTypeDto.getCode());
//        carType.setDescription(carTypeDto.getDescription());
//        carTypeRespository.save(carType);
        return "redirect:/carTypes";

    }
    @GetMapping("/carTypesEdit")
    public String edit(Model model, @RequestParam int id) {
//        CarType carType= carTypeRespository.findById(id).orElse(null);
//        if (carType==null) {
//            return "redirect:/carTypes";
//        }
//        CarTypeDto carTypeDto=new CarTypeDto();
//        carTypeDto.setBrand(carType.getBrand());
//        carTypeDto.setName(carType.getName());
//        carTypeDto.setCode(carType.getCode());
//        carTypeDto.setDescription(carType.getDescription());
//        model.addAttribute("carType",carType);
//        model.addAttribute("carTypeDto",carTypeDto);
        return "carTypesEdit";
    }
    @PostMapping("/carTypesEdit")
    public String editCarType(Model model,
                              @RequestParam int id,
                              @Valid @ModelAttribute("carTypeDto")
                                  CarTypeDto carTypeDto,
                              BindingResult result) {
//        CarType existingCarType= carTypeRespository.findById(id).orElse(null);
//        if (existingCarType==null) {
//            return "redirect:/carTypes";
//        }
//        if (result.hasErrors()) {
//            model.addAttribute("carTypeDto",existingCarType);
//            return "carTypesEdit";
//        }
//        existingCarType.setName(carTypeDto.getName());
//        existingCarType.setBrand(carTypeDto.getBrand());
//        existingCarType.setCode(carTypeDto.getCode());
//        existingCarType.setDescription(carTypeDto.getDescription());
//        carTypeRespository.save(existingCarType);
        return "redirect:/carTypes";
    }
    @GetMapping("/carTypesDelete")
    public String delete( @RequestParam int id) {
//        CarType existingCarType= carTypeRespository.findById(id).orElse(null);
//        if (existingCarType!=null) {
//            carTypeRespository.deleteById(id);
//        }
        return "redirect:/carTypes";
    }


}
