package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.UserAccount;
import com.turbopick.autowise.service.UserAccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class UserAccountController {

    private final UserAccountService service;

    public UserAccountController(UserAccountService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", service.findAll());
        return "admin/userList";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("user", new UserAccount());
        return "admin/userCreate";
    }

    @PostMapping("/create")
    public String createSubmit(@ModelAttribute UserAccount form) {
        // TODO: hash form.getPasswordHash() before save
        service.save(form);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", service.findByIdOrThrow(id));
        return "admin/userEdit";
    }

    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Long id, @ModelAttribute UserAccount form) {
        UserAccount u = service.findByIdOrThrow(id);
        u.setName(form.getName());
        u.setEmail(form.getEmail());
        // TODO: hash new password if provided
        u.setPasswordHash(form.getPasswordHash());
        u.setRole(form.getRole() != null ? form.getRole() : u.getRole());
        service.save(u);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/admin/users";
    }
}