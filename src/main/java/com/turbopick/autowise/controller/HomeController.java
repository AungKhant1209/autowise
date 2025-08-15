package com.turbopick.autowise.controller;
import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private CarService carService;

    @GetMapping("/home")
    public String homePage(Model model) {
        // Fetch the list of cars and add them to the model
        List<Car> cars = carService.getAllCars();
        model.addAttribute("cars", cars);
        return "home"; // This will return the 'home.html' template
    }
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("name", "Aung Khant");
        return "login";
    }
    @GetMapping("/sign-up")
    public String signup(Model model) {
        model.addAttribute("name", "Aung Khant");
        return "sign-up";
    }

}