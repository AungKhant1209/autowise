package com.turbopick.autowise.service;

import com.turbopick.autowise.model.UserAccount;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountService userAccountService;

    public CustomUserDetailsService(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount ua = userAccountService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(ua.getEmail())
                .password(ua.getPasswordHash())
                .authorities(new SimpleGrantedAuthority(ua.getRole()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}