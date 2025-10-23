package com.example.saratogapizza.responses;

import com.example.saratogapizza.entities.User;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class GetCouponsResponse {


    private Long id;

    private String code;

    private double discountPercentage;

    private LocalDate validityStartDate;

    private LocalDate validityEndDate;

    private double minOrderValue;

    private boolean isActive = true;

    private Set<UserToUseThisCouponResponse> usedByUsers = new HashSet<>();
}
