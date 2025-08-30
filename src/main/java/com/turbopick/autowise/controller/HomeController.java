package com.turbopick.autowise.controller;
import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.service.CarTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private CarTypeService carTypeService;

    @GetMapping("/home")
    public String hello(Model model) {
        model.addAttribute("name", "Aung Khant");
        List<CarType> carTypes=carTypeService.findAllCarTypes();
        model.addAttribute("carTypes",carTypes);
        System.out.println("CarTypeController.carList"+ carTypes.size());
        return "home";
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