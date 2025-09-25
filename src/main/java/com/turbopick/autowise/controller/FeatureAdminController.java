package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.Feature;
import com.turbopick.autowise.service.FeatureService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/featureList")
public class FeatureAdminController {

    private final FeatureService featureService;

    public FeatureAdminController(FeatureService featureService) {
        this.featureService = featureService;
    }

    // LIST
    @GetMapping
    public String list(Model model) {
        model.addAttribute("features", featureService.findAll());
        return "admin/featureList";
    }

    // EDIT form
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("feature", featureService.findByIdOrThrow(id));
        return "admin/featureEdit";
    }

    // EDIT submit
    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id,
                             @Valid @ModelAttribute("feature") Feature form,
                             BindingResult result,
                             RedirectAttributes ra,
                             Model model) {
        try {
            if (!result.hasErrors()) {
                form.setId(id);          // ensure we update
                featureService.save(form);
                ra.addFlashAttribute("ok", "Feature updated.");
                return "redirect:/admin/featureList";
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            result.rejectValue("name", "invalid", ex.getMessage());
        }
        model.addAttribute("feature", form);
        return "admin/featureEdit";
    }

    // DELETE
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        featureService.deleteById(id);
        ra.addFlashAttribute("ok", "Feature deleted.");
        return "redirect:/admin/featureList";
    }
}