package com.turbopick.autowise.controller;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String hello(Model model) {
        model.addAttribute("name", "Aung Khant");
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