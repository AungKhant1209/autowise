package com.turbopick.autowise.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class RoleAwareSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
                                        Authentication auth) throws IOException, ServletException {
        String selectedRole = req.getParameter("selectedRole"); // may be null
        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        if (selectedRole != null && !roles.contains(selectedRole)) {
            res.sendRedirect(req.getContextPath() + "/login?roleMismatch");
            return;
        }

        String target;
        if ("ROLE_ADMIN".equals(selectedRole)) {
            target = "/admin/index";
        } else {
            target = "/home";               // 👈 shared home for both roles
        }
        res.sendRedirect(req.getContextPath() + target);
    }
}