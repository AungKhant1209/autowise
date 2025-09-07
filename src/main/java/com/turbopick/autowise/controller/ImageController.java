package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.repository.CarRepository;
import com.turbopick.autowise.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class ImageController {

    private final S3Service s3Service;
    private final CarRepository carRepository;

    @GetMapping("/imageUpload")
    public String imageUploadForm(@RequestParam(value = "carId", required = false) Long carId, Model model) {
        model.addAttribute("cars", carRepository.findAll());
        if (carId != null) {
            model.addAttribute("selectedCarId", carId);
            model.addAttribute("imageUrls",
                    carRepository.findById(carId).map(Car::getImageUrls).orElseGet(java.util.List::of));
        }
        return "admin/imageUpload";
    }

    @PostMapping("/imageUpload")
    @Transactional
    public String uploadImage(@RequestParam("files") MultipartFile[] files,
                              @RequestParam("carId") Long carId,
                              RedirectAttributes ra) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found: " + carId));

        long nonEmpty = Arrays.stream(files).filter(f -> !f.isEmpty()).count();
        if (nonEmpty == 0) {
            ra.addFlashAttribute("error", "Please choose at least one image.");
            return "redirect:/admin/imageUpload?carId=" + carId;
        }

        try {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String ct = file.getContentType();
                if (ct == null || !ct.startsWith("image/")) {
                    ra.addFlashAttribute("error", "Only image files are allowed.");
                    return "redirect:/admin/imageUpload?carId=" + carId;
                }

                String url = s3Service.uploadFile(file); // must be public/permanent
                if (url == null || url.isBlank()) {
                    ra.addFlashAttribute("error", "Upload failed: empty URL returned from S3 service.");
                    return "redirect:/admin/imageUpload?carId=" + carId;
                }
                car.getImageUrls().add(url);
            }

            carRepository.save(car);
            ra.addFlashAttribute("message", "Images uploaded successfully!");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }

        return "redirect:/admin/imageUpload?carId=" + carId;
    }
}
