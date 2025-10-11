package com.example.saratogapizza.requests;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductSizeRequest {
    private String sizeName; // S,M,L,XL

    private BigDecimal additionalPrice; // Basic Price + this = last price

    private Long productId;


}
