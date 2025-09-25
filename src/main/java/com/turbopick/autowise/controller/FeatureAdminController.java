package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.Feature;
import com.turbopick.autowise.repository.FeatureRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/features")
public class FeatureAdminController {

    private final FeatureRepository featureRepository;

    public FeatureAdminController(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    // ===== LIST =====
    @GetMapping
    public String list(Model model, @ModelAttribute("ok") String okMsg) {
        model.addAttribute("features", featureRepository.findAll());
        return "admin/feature-list";
    }

    // ===== CREATE (form) =====
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("feature", new Feature());
        model.addAttribute("formTitle", "Create Feature");
        model.addAttribute("formAction", "/admin/features"); // POST create
        return "admin/feature-form";
    }

    // ===== CREATE (submit) =====
    @PostMapping
    public String createSubmit(@ModelAttribute("feature") Feature form,
                               BindingResult result,
                               RedirectAttributes ra,
                               Model model) {

        // (Optional) simple checks; add Bean Validation on entity if you want
        if (isBlank(form.getName()) || isBlank(form.getCategory())) {
            result.reject("field.missing", "Name and Category are required.");
        }
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Create Feature");
            model.addAttribute("formAction", "/admin/features");
            return "admin/feature-form";
        }

        featureRepository.save(form);
        ra.addFlashAttribute("ok", "Feature created.");
        return "redirect:/admin/features";
    }

    // ===== EDIT (form) =====
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + id));
        model.addAttribute("feature", feature);
        model.addAttribute("formTitle", "Edit Feature");
        model.addAttribute("formAction", "/admin/features/" + id); // POST update
        return "admin/feature-form";
    }

    // ===== EDIT (submit) =====
    @PostMapping("/{id}")
    public String editSubmit(@PathVariable Long id,
                             @ModelAttribute("feature") Feature form,
                             BindingResult result,
                             RedirectAttributes ra,
                             Model model) {

        if (isBlank(form.getName()) || isBlank(form.getCategory())) {
            result.reject("field.missing", "Name and Category are required.");
        }
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Edit Feature");
            model.addAttribute("formAction", "/admin/features/" + id);
            return "admin/feature-form";
        }

        Feature existing = featureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + id));

        existing.setName(form.getName());
        existing.setCategory(form.getCategory());
        existing.setDescription(form.getDescription());
        // cars is ManyToMany on Car side; we don't touch it here.

        featureRepository.save(existing);
        ra.addFlashAttribute("ok", "Feature updated.");
        return "redirect:/admin/features";
    }

    // ===== DELETE =====
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Optional<Feature> existing = featureRepository.findById(id);
        if (existing.isPresent()) {
            featureRepository.deleteById(id);
            ra.addFlashAttribute("ok", "Feature deleted.");
        } else {
            ra.addFlashAttribute("ok", "Feature not found (maybe already deleted).");
        }
        return "redirect:/admin/features";
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}