package com.turbopick.autowise.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("compareCount")
    public Integer compareCount(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) session.getAttribute("compareIds");
        return (ids == null) ? 0 : ids.size();
    }
}