package com.example.saratogapizza.responses;

import com.example.saratogapizza.entities.Product;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSizeResponse {

    private Long id;

    private String sizeName; // S,M,L,XL

    private BigDecimal additionalPrice; // Basic Price + this = last price


    //private Product product;
}
