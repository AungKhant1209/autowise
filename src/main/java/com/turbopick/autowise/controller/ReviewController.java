// src/main/java/com/turbopick/autowise/controller/ReviewController.java
package com.turbopick.autowise.controller;

import com.turbopick.autowise.dto.ReviewDto;
import com.turbopick.autowise.model.User;
import com.turbopick.autowise.model.UserAccount;
import com.turbopick.autowise.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReviewController {
    @Autowired
    private FeatureService featureService;

    @Autowired
    private CarService carService;

    @Autowired
    private CarTypeService carTypeService;

    @Autowired
    private CarBrandService carBrandService;

    @Autowired
    private S3Service s3Service;

   @Autowired
   private ReviewService reviewService;
   @Autowired
   private UserAccountService userAccountService;

    // Show car detail WITH reviews + form
    @GetMapping("/cars/{id}")
    public String carDetail(@PathVariable Long id, Model model) {
        var car = carService.findByIdOrNull(id);
        if (car == null) return "redirect:/cars";

        model.addAttribute("car", car);
        model.addAttribute("reviews", reviewService.listForCar(id));
        model.addAttribute("avgRating", reviewService.averageForCar(id));
        model.addAttribute("reviewDto", new ReviewDto());
        return "car-detail"; // your detail page
    }

    @PostMapping("/cars/{id}/reviews11")
    public String submitReview(@PathVariable Long id,
                               @Valid @ModelAttribute("reviewDto") ReviewDto reviewDto,
                               BindingResult result,
                               @AuthenticationPrincipal(expression = "username") String email,
                               Model model) {

        if (email == null) {
            return "redirect:/login";
        }

        UserAccount user = userAccountService.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("car", carService.findByIdOrNull(id));
            model.addAttribute("reviews", reviewService.listForCar(id));
            model.addAttribute("avgRating", reviewService.averageForCar(id));
            return "car-detail";
        }

        try {
            reviewService.addReview(id, user.getId(), reviewDto);
        } catch (IllegalStateException dup) {
            result.rejectValue("comment", "duplicate", "You already reviewed this car.");
            model.addAttribute("car", carService.findByIdOrNull(id));
            model.addAttribute("reviews", reviewService.listForCar(id));
            model.addAttribute("avgRating", reviewService.averageForCar(id));
            return "car-detail";
        }

        return "redirect:/cars/" + id;
    }
}