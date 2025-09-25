package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.Feature;
import com.turbopick.autowise.service.FeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/features")
@RequiredArgsConstructor
public class FeatureAdminController {

    private final FeatureService featureService;

    // Static category options (simple + reliable). Swap to repo-backed if you prefer.
    @ModelAttribute("categoryOptions")
    public List<String> categoryOptions() {
        return Arrays.asList("Safety","Comfort","Technology","Performance","Exterior","Interior","Infotainment","Driver Assistance");
    }

    // ===== LIST =====
    @GetMapping
    public String list(Model model) {
        model.addAttribute("features", featureService.findAll());
        return "admin/featureList";
    }

    // ===== CREATE (form) =====
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("feature", new Feature());
        return "admin/featureCreate";
    }

    // (Legacy alias if you used /admin/featureCreate before)
    @GetMapping("/../featureCreate")
    public String legacyCreateAlias() { return "redirect:/admin/features/create"; }

    // ===== CREATE (submit) =====
    @PostMapping
    public String createSubmit(@ModelAttribute("feature") Feature form,
                               BindingResult result,
                               Model model,
                               RedirectAttributes ra) {
        try {
            featureService.save(form);
            ra.addFlashAttribute("ok", "Feature created.");
            return "redirect:/admin/features";
        } catch (RuntimeException ex) {
            // Show service validation/uniqueness errors
            model.addAttribute("err", ex.getMessage());
            return "admin/featureCreate";
        }
    }

    // ===== EDIT (form) =====
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("feature", featureService.findByIdOrThrow(id));
        return "admin/featureEdit";
    }

    // ===== EDIT (submit) =====
    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id,
                             @ModelAttribute("feature") Feature form,
                             BindingResult result,
                             Model model,
                             RedirectAttributes ra) {
        try {
            form.setId(id);                 // your service save() handles create/update by id
            featureService.save(form);
            ra.addFlashAttribute("ok", "Feature updated.");
            return "redirect:/admin/features";
        } catch (RuntimeException ex) {
            model.addAttribute("err", ex.getMessage());
            return "admin/featureEdit";
        }
    }

    // ===== DELETE =====
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        featureService.deleteById(id);
        ra.addFlashAttribute("ok", "Feature deleted.");
        return "redirect:/admin/features";
    }
}