package com.turbopick.autowise.config;

import com.turbopick.autowise.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public DaoAuthenticationProvider authProvider(CustomUserDetailsService uds, BCryptPasswordEncoder enc) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(enc);
        return p;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider provider) throws Exception {
        http.authenticationProvider(provider);

        http.authorizeHttpRequests(auth -> auth
                // public assets & public pages
                .requestMatchers(
                        "/assets/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico"
                ).permitAll()
                .requestMatchers("/", "/home", "/about", "/contact").permitAll()

                // login pages must be public to render (both share the same POST /login)
                .requestMatchers("/login", "/register", "/admin/login",
                        "/assets/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/cars/**", "/car-detail/**").hasAnyRole("USER","ADMIN")


                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login")                 // single login endpoint
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(new RoleBasedSuccessHandler()) // ADMIN → /admin, else → /
                .failureUrl("/login?error")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
        );

        http.csrf(Customizer.withDefaults());
        http.exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        return http.build();
    }
}
