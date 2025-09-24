package com.turbopick.autowise.controller;
import com.turbopick.autowise.dto.RegisterDto;
import com.turbopick.autowise.model.CarType;
import com.turbopick.autowise.model.UserAccount;
import com.turbopick.autowise.repository.UserAccountRepository;
import com.turbopick.autowise.service.CarTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {
    private final CarTypeService carTypeService;

    public HomeController(CarTypeService carTypeService) {
        this.carTypeService = carTypeService;
    }
    @GetMapping("/home")
    public String home(Model model, Authentication auth) {
        // optional: inspect who’s logged in, load shared data, etc.
        model.addAttribute("name", auth != null ? auth.getName() : "Guest");
        return "home"; // templates/home.html
    }

//    @GetMapping("/user/home")
//    public String userHome(Model model) {
//        model.addAttribute("name", "Aung Khant");
//        model.addAttribute("carTypes", carTypeService.findAll());
//        return "home"; // templates/home.html
//    }
//
//    @GetMapping("/hello_testing")
//    public String testing(Model model) {
//        model.addAttribute("name", "Aung Khant");
//        return "hello_testing";
//    }
}