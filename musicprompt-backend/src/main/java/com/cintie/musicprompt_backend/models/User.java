package com.cintie.musicprompt_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Column(unique = true, nullable = false)
    @Email(message = "Email should be valid")
    private String email;

    @Column(nullable = false)
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-]*$",
            message = "Password must contain only Latin letters, numbers and special characters"
    )
    private String password;

    @Column(nullable = false)
    private String role = "USER";
}