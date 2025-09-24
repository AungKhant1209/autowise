package com.turbopick.autowise.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class RoleAwareSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
                                        Authentication auth) throws IOException, ServletException {

        String selectedRole = req.getParameter("selectedRole"); // "ROLE_USER" or "ROLE_ADMIN"
        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // If user chose a side they don't have, send back to login
        if (selectedRole != null && !selectedRole.isBlank() && !roles.contains(selectedRole)) {
            res.sendRedirect(req.getContextPath() + "/login?roleMismatch");
            return;
        }

        // Determine landing page
        String target;
        if ("ROLE_ADMIN".equals(selectedRole) || (selectedRole == null && roles.contains("ROLE_ADMIN"))) {
            target = "/admin/index";         // → templates/admin/index.html
        } else {
            target = "/user/home";           // → templates/home.html
        }

        res.sendRedirect(req.getContextPath() + target);
    }
}