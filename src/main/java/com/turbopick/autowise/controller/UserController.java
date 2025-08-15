//package com.turbopick.autowise.controller;
//
//
//import com.turbopick.autowise.model.UserDto;
//import com.turbopick.autowise.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.ui.Model;
//
//@Controller
//@RequestMapping("/users")
//public class UserController {
//    @Autowired
//    private UserRepository userRepository;
//
//    @GetMapping("/registerUser")
//    public String registerUser(Model model) {
//        UserDto userDto = new UserDto();
//        model.addAttribute("userDto", userDto);
//        return "sign-up";
//    }
//}
