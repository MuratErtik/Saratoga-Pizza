package com.example.saratogapizza.responses;


import com.example.saratogapizza.entities.*;

import lombok.*;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class GetCustomerInfoResponse {

        private String name;

        private String lastname;

        private String email;

        private boolean isVerified;

        private String mobileNo;

        private LocalDateTime createdAt;

        private LocalDateTime lastLoginAt;

        private Set<GetCustomerAddressesResponse> addresses = new HashSet<>();

        private Set<GetCustomerBankDetailsResponse> bankDetails = new HashSet<>();

        private Set<Order> orders = new HashSet<>();

        private Set<Review> reviews = new HashSet<>();

        private Set<Coupon> usedCoupons = new HashSet<>();

        private int loyaltyPoints;


}
