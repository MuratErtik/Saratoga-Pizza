package com.example.saratogapizza.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_sizes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sizeName; // S,M,L,XL

    private BigDecimal additionalPrice; // Basic Price + this = last price

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}

