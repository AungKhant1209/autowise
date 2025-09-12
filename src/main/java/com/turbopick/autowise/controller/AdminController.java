package com.turbopick.autowise.controller;

import org.springframework.ui.Model;
import com.turbopick.autowise.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private CarRepository carRepository;

    @GetMapping({"", "/"})
    public String showAdminPage(Model model) {
        var cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "admin"; // Render the admin.html page
    }

    @GetMapping("/cars")
    public String getCars(Model model) {
        var cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "car-list";  // Assuming you already have a car-list template
    }

    // Similarly for car types
    @GetMapping("/carTypes")
    public String getCarTypes(Model model) {
        // Logic for car types
        return "car-type-list"; // Assuming you have a template for car types
    }

    // Other car management endpoints for register, update, etc.
}
