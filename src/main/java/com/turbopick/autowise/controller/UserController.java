package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.User;
import com.turbopick.autowise.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", service.findAll());
        return "admin/userList";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/userCreate";
    }

    @PostMapping("/create")
    public String createSubmit(@ModelAttribute User user) {
        service.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", service.findById(id));
        return "admin/userEdit";
    }

    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Long id, @ModelAttribute User form) {
        User u = service.findById(id);
        u.setName(form.getName());
        u.setEmail(form.getEmail());
        u.setPassword(form.getPassword());
        service.save(u);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/admin/users";
    }
}
