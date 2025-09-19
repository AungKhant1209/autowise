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

    /* ========= Create / Update ========= */

    /**
     * Saves a user. If a plain text password is provided, it will be encoded.
     * If password is blank on update, keeps the existing hash.
     */
    @Transactional
    public UserAccount save(UserAccount ua) {
        if (ua.getPasswordHash() != null && !ua.getPasswordHash().isBlank()) {
            // encode only if it doesn't look already encoded
            if (!looksEncoded(ua.getPasswordHash())) {
                ua.setPasswordHash(passwordEncoder.encode(ua.getPasswordHash()));
            }
        } else if (ua.getId() != null) {
            // keep existing hash if password not provided during edit
            String existing = repo.findById(ua.getId())
                    .map(UserAccount::getPasswordHash)
                    .orElse(null);
            ua.setPasswordHash(existing);
        }
        return repo.save(ua);
    }

    private boolean looksEncoded(String pw) {
        // Common bcrypt prefixes
        return pw.startsWith("{bcrypt}") || pw.startsWith("$2a$")
                || pw.startsWith("$2b$") || pw.startsWith("$2y$");
    }

    /* ========= Read ========= */

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

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    /* ========= Delete ========= */

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
    public UserAccount findByEmailOrThrow(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user " + email));
    }
}