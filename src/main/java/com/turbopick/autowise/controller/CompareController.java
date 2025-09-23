package com.turbopick.autowise.controller;

import com.turbopick.autowise.model.Car;
import com.turbopick.autowise.service.CarService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/compare")
public class CompareController {
    @Autowired
    private CarService carService;
    @GetMapping
    public String showCompare(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) session.getAttribute("compareIds");
        if (ids == null) ids = new java.util.ArrayList<>();

        List<Car> cars = ids.isEmpty() ? java.util.Collections.emptyList()
                : carService.findAllById(ids);
        model.addAttribute("cars", cars);

        // keep session count in sync (handy if something changed)
        session.setAttribute("compareCount", ids.size());
        return "compare";
    }

    @PostMapping("/add")
    public String addToCompare(@RequestParam Long id, HttpSession session,
                               HttpServletRequest req) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) session.getAttribute("compareIds");
        if (ids == null) ids = new java.util.ArrayList<>();
        if (!ids.contains(id)) ids.add(id);
        session.setAttribute("compareIds", ids);
        session.setAttribute("compareCount", ids.size());

        // go back to where user clicked "Compare"
        String referer = req.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/compare");
    }

    @PostMapping("/remove")
    public String removeFromCompare(@RequestParam Long id, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) session.getAttribute("compareIds");
        if (ids != null) {
            ids.remove(id);
            session.setAttribute("compareIds", ids);
            session.setAttribute("compareCount", ids.size());
        }
        return "redirect:/compare";
    }
}