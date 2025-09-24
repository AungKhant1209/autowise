package com.turbopick.autowise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Login page (your hello_testing.html contains the login <section>)
    @GetMapping("/login")
    public String login() {
        return "hello_testing"; // templates/hello_testing.html
    }

    // Your test route
    @GetMapping("/hello_testing1")
    public String testing(Model model) {
        model.addAttribute("name", "Aung Khant");
        return "hello_testing";
    }

    // USER landing
    @GetMapping("/user/home")
    public String userHome() {
        return "home"; // templates/home.html
    }

    // ADMIN landing
    @GetMapping("/admin/index")
    public String adminIndex() {
        return "admin/index"; // templates/admin/index.html
    }
}