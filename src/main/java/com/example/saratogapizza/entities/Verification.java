package com.example.saratogapizza.entities;

import com.example.saratogapizza.domains.VerificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    private String verificationCode; // 6 digit code or UUID token

    @Enumerated(EnumType.STRING)
    private VerificationType type; // EMAIL, PHONE, PASSWORD_RESET

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime expiresAt;

    private boolean verified = false; //if it's true it had been used.
}
