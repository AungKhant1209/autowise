package com.turbopick.autowise.service;

import com.turbopick.autowise.model.User;
import com.turbopick.autowise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Delete a user by ID
    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }

    // Find a user by ID
    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
