package com.example.saratogapizza.requests;

import com.example.saratogapizza.entities.Product;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductSizeRequest {

    private String sizeName; // S,M,L,XL

    private BigDecimal additionalPrice; // Basic Price + this = last price

    private Long productId;
}



