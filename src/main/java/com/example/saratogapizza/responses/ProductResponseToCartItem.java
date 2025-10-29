package com.example.saratogapizza.responses;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponseToCartItem {

    private Long id;

    private String sizeName; // S,M,L,XL

    private BigDecimal additionalPrice; // Basic Price + this = last price

    private String name;

    private BigDecimal price;

}
