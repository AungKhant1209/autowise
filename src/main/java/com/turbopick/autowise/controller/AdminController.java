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

    @GetMapping({"", "/"})
    public String showAdminPage(Model model) {
        var cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "admin";
    }


}
