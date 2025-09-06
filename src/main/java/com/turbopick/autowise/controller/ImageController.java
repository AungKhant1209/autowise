package com.turbopick.autowise.controller;
import com.turbopick.autowise.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class ImageController {

    private final S3Service s3Service;

    @GetMapping("/imageUpload")
    public String index() {
        return "admin/imageUpload";
    }

    @PostMapping("/imageUpload")
    public String uploadImage(@RequestParam("file") MultipartFile file, Model model) {
        System.out.println("File name: " + file.getOriginalFilename());
        try {
            String imageUrl = s3Service.uploadFile(file);
            //need to strore url in database--> need to fix to upload for multiple images
            // one care have many images


            model.addAttribute("imageUrl", imageUrl);
            model.addAttribute("message", "Upload successful!");
        } catch (Exception e) {
            model.addAttribute("message", "Upload failed: " + e.getMessage());
        }
        return "admin/imageUpload";
    }
}

