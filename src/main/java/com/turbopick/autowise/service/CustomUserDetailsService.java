package com.turbopick.autowise.service;

import com.turbopick.autowise.model.UserAccount;
import com.turbopick.autowise.repository.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserAccountRepository repo;

    public CustomUserDetailsService(UserAccountRepository repo) {
        this.repo = repo;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount ua = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user " + email));
        return new org.springframework.security.core.userdetails.User(
                ua.getEmail(),
                ua.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(ua.getRole()))
        );
    }
}
