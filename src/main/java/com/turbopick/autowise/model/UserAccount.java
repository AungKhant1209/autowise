package com.turbopick.autowise.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "user_accounts", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name required")
    private String name;

    @Email @NotBlank(message = "Email required")
    private String email;

    @NotBlank(message = "Password required")
    private String passwordHash; // store the hash

    // very simple role model; you can expand to many-to-many
    @Column(nullable = false)
    private String role = "ROLE_USER";
}