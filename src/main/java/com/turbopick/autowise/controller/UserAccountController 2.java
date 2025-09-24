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

    // Constructor-based Dependency Injection for UserAccountService
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    // List all users
    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userAccountService.findAll());
        return "admin/userList";  // Points to userList.html
    }

    // View a specific user
    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userAccountService.findByIdOrThrow(id));
        return "admin/userView";  // Points to userView.html
    }

    // Create a new user (GET)
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("user", new UserAccount());
        return "admin/userCreate";  // Points to userCreate.html
    }

    // Create a new user (POST)
    @PostMapping("/create")
    public String createSubmit(@Valid @ModelAttribute UserAccount user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/userCreate";  // Return to form if there are validation errors
        }
        userAccountService.save(user);
        return "redirect:/admin/users";  // Redirect to the user list after successful creation
    }

    // Edit user (GET)
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        UserAccount user = userAccountService.findByIdOrThrow(id);
        model.addAttribute("user", user);
        return "admin/userEdit";  // Points to userEdit.html
    }

    // Edit user (POST)
    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Long id, @Valid @ModelAttribute UserAccount form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/userEdit";  // Return to the form if there are validation errors
        }

        UserAccount existingUser = userAccountService.findByIdOrThrow(id);
        existingUser.setName(form.getName());
        existingUser.setEmail(form.getEmail());
        existingUser.setPasswordHash(form.getPasswordHash());  // Update password if provided
        existingUser.setRole(form.getRole() != null ? form.getRole() : existingUser.getRole());
        existingUser.setActive(form.isActive());  // Update active status

        userAccountService.save(existingUser);  // Save the updated user details
        return "redirect:/admin/users";  // Redirect to the user list page after successful update
    }

    // Delete user (using POST to override DELETE method)
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        userAccountService.delete(id);
        return "redirect:/admin/users";  // Redirect to user list after successful deletion
    }
}
