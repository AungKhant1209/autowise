package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.Feature;
import com.turbopick.autowise.service.FeatureService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")  // <<-- important so /admin/featureCreate resolves
public class FeatureAdminController {

    private final FeatureService featureService;

    public FeatureAdminController(FeatureService featureService) {
        this.featureService = featureService;
    }

    // ===== CREATE (form) =====
    @GetMapping("/featureCreate")
    public String createForm(Model model) {
        model.addAttribute("feature", new Feature());
        return "admin/featureCreate";
    }

    // ===== CREATE (submit) =====
    @PostMapping("/features")
    public String createSubmit(@Valid @ModelAttribute("feature") Feature form,
                               BindingResult result,
                               RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/featureCreate";
        }
        featureService.save(form);
        ra.addFlashAttribute("ok", "Feature created.");
        return "redirect:/admin/featureList";
    }

    // ===== LIST (for Cancel button target) =====
    @GetMapping("/featureList")
    public String featureList(Model model) {
        model.addAttribute("features", featureService.findAll());
        return "admin/featureList";
    }

    // ===== EDIT (form) =====
    @GetMapping("/features/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("feature", featureService.findByIdOrThrow(id));
        return "admin/featureEdit";
    }

    // ===== EDIT (submit) =====
    @PostMapping("/features/{id}/edit")
    public String editSubmit(@PathVariable Long id,
                             @Valid @ModelAttribute("feature") Feature form,
                             BindingResult result,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/featureEdit";
        }
        form.setId(id);
        featureService.save(form);
        ra.addFlashAttribute("ok", "Feature updated.");
        return "redirect:/admin/featureList";
    }
    // ===== DELETE =====
    @PostMapping("/features/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        featureService.deleteById(id);
        ra.addFlashAttribute("ok", "Feature deleted.");
        return "redirect:/admin/featureList";
    }
}