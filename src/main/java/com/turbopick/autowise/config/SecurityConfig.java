package com.turbopick.autowise.config;

import com.turbopick.autowise.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authProvider(CustomUserDetailsService uds, BCryptPasswordEncoder enc) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(enc);
        return p;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider provider) throws Exception {
        http.authenticationProvider(provider);

        // Authorization Rules
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/login","/compare", "/register", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/admin/cars/*/delete").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")   // Only ADMIN can access /admin paths
                .requestMatchers("/user/**").hasRole("USER")     // Only USER can access user-specific paths (if needed)
                .anyRequest().authenticated()  // All other requests require authentication
        );

        // Login Configuration
        http.formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    if (isAdmin) {
                        response.sendRedirect("/");
                    } else {
                        response.sendRedirect("/home");
                    }
                })
                .failureUrl("/login?error")
                .permitAll()
        );


        // Logout Configuration
        http.logout(logout -> logout
                .logoutUrl("/logout")  // Default logout URL
                .logoutSuccessUrl("/login?logout")  // Redirect to login page after successful logout
                .invalidateHttpSession(true)  // Invalidate the session
                .clearAuthentication(true)  // Clear authentication
                .permitAll()
        );

        // CSRF Protection
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/admin/**")  // Optional: Ignore CSRF for admin paths if required
        );

        return http.build();
    }
}
