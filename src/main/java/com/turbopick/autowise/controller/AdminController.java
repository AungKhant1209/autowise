package com.turbopick.autowise.controller;

import com.turbopick.autowise.repository.CarRepository;
import com.turbopick.autowise.repository.CarBrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarBrandRepository carBrandRepository;

//    @GetMapping({"", "/"})
//    public String showAdminPage(Model model) {
//        var cars = carRepository.findAll();
//        model.addAttribute("cars", cars);
//        return "admin";
//    }


    @GetMapping("/cars")
    public String getCars(Model model) {
        var cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "car-list";
    }


    @GetMapping("/carTypes")
    public String getCarTypes(Model model) {
        // TODO: add logic for car types
        return "car-type-list";
    }


    @GetMapping("/carBrands")
    public String getCarBrands(Model model) {
        var brands = carBrandRepository.findAll();
        model.addAttribute("carBrands", brands);
        return "car-brand-list"; // Create car-brand-list.html template
    }
}
