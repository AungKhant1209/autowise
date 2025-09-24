package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.UserAccount;
import com.turbopick.autowise.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class UserAccountController {

    private final UserAccountService userAccountService;


    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }


    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userAccountService.findAll());
        return "admin/userList";  // Points to userList.html
    }


    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userAccountService.findByIdOrThrow(id));
        return "admin/userView";  // Points to userView.html
    }


    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("user", new UserAccount());
        return "admin/userCreate";
    }


    @PostMapping("/create")
    public String createSubmit(@Valid @ModelAttribute UserAccount user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/userCreate";
        }
        userAccountService.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        UserAccount user = userAccountService.findByIdOrThrow(id);
        model.addAttribute("user", user);
        return "admin/userEdit";
    }


    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Long id, @Valid @ModelAttribute UserAccount form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/userEdit";
        }

        UserAccount existingUser = userAccountService.findByIdOrThrow(id);
        existingUser.setName(form.getName());
        existingUser.setEmail(form.getEmail());
        existingUser.setPasswordHash(form.getPasswordHash());  // Update password if provided
        existingUser.setRole(form.getRole() != null ? form.getRole() : existingUser.getRole());
        existingUser.setActive(form.isActive());

        userAccountService.save(existingUser);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        userAccountService.delete(id);
        return "redirect:/admin/users";
    }
}
