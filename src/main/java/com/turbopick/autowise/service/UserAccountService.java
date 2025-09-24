package com.turbopick.autowise.service;

import com.turbopick.autowise.model.UserAccount;
import com.turbopick.autowise.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class UserAccountService {

    private final UserAccountRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public UserAccount save(UserAccount ua) {
        if (ua.getPasswordHash() != null && !ua.getPasswordHash().isBlank()) {
            if (!looksEncoded(ua.getPasswordHash())) {
                ua.setPasswordHash(passwordEncoder.encode(ua.getPasswordHash()));
            }
        } else if (ua.getId() != null) {
            String existing = repo.findById(ua.getId())
                    .map(UserAccount::getPasswordHash)
                    .orElse(null);
            ua.setPasswordHash(existing);
        }
        return repo.save(ua);
    }

    private boolean looksEncoded(String pw) {

        return pw.startsWith("{bcrypt}") || pw.startsWith("$2a$")
                || pw.startsWith("$2b$") || pw.startsWith("$2y$");
    }



    @Transactional(readOnly = true)
    public List<UserAccount> findAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> findById(Long id) {
        return repo.findById(id);
    }

    @Transactional(readOnly = true)
    public UserAccount findByIdOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserAccount not found: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> findByEmail(String email) {
        return repo.findByEmail(email);
    }


    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
    public UserAccount findByEmailOrThrow(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user " + email));
    }
}