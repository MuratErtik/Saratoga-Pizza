package com.example.saratogapizza.responses;

import com.example.saratogapizza.entities.CartItem;
import com.example.saratogapizza.entities.Coupon;
import com.example.saratogapizza.entities.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class GetCustomerCartResponse {

    //private Long id;

    //private User user;

    private Set<CartItemResponseToCart> cartItems = new HashSet<>();

    private BigDecimal totalSellingPrice;

    private int totalItem;

    private BigDecimal discount;

    private CouponResponseToCart  coupon;

    //private LocalDateTime createdAt;

    //private LocalDateTime updatedAt;

    //private boolean checkedOut;
}
