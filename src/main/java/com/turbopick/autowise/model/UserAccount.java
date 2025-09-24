package com.turbopick.autowise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "user_accounts", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name required")
    private String name;

    @Email
    @NotBlank(message = "Email required")
    private String email;

    @NotBlank(message = "Password required")
    private String passwordHash;

    @Column(nullable = false)
    private String role = "ROLE_USER";

    @Column(nullable = false)
    private boolean active = true;  // Add this field to track active status

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Review> reviews = new ArrayList<>();

    // Getter and Setter methods for 'active' field
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
