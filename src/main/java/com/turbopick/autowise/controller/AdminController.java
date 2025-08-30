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

    @GetMapping({"","/"})
    public String getCars(Model model) {
        var cars = carRepository.findAll();
        model.addAttribute("cars", cars);
        return "car-list";
    }
}
