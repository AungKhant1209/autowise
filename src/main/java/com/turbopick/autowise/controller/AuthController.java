package com.turbopick.autowise.controller;

import com.turbopick.autowise.dto.RegisterDto;
import com.turbopick.autowise.model.UserAccount;
import com.turbopick.autowise.repository.UserAccountRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserAccountRepository repo;
    private final BCryptPasswordEncoder encoder;

    public AuthController(UserAccountRepository repo, BCryptPasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @GetMapping("/login")
    public String login() {
        return "sign-in"; // templates/sign-in.html
    }

    @GetMapping("/register")
    public String register(Model model) {
        RegisterDto dto = new RegisterDto();
        dto.setRole("USER"); // default
        model.addAttribute("registerDto", dto);
        return "sign-up";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("registerDto") RegisterDto dto,
                             BindingResult result) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.addError(new FieldError("registerDto", "confirmPassword", "Passwords do not match"));
        }
        if (repo.existsByEmail(dto.getEmail())) {
            result.addError(new FieldError("registerDto", "email", "Email already in use"));
        }
        if (result.hasErrors()) return "sign-up";

        UserAccount ua = new UserAccount();
        ua.setName(dto.getName());
        ua.setEmail(dto.getEmail());
        ua.setPasswordHash(encoder.encode(dto.getPassword()));
        ua.setRole("ADMIN".equalsIgnoreCase(dto.getRole()) ? "ROLE_ADMIN" : "ROLE_USER");

        repo.save(ua);
        return "redirect:/login?registered";
    }

}