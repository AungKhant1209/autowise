package com.turbopick.autowise.repository;

import com.turbopick.autowise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
