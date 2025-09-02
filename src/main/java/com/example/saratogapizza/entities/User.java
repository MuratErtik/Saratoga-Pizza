package com.example.saratogapizza.entities;

import com.example.saratogapizza.domains.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.ROLE_CUSTOMER;

    private String name;

    private String lastname;

    @Column(unique = true, nullable = false)
    private String email;

    private boolean isVerified = false;

    @Column(nullable = false)
    private String password;

    private String  mobileNo;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    @OneToMany
    private Set<Address> addresses = new HashSet<>();

    @ManyToMany
    private Set<Coupon> usedCoupons = new HashSet<>();

    int loyaltyPoints;



}
