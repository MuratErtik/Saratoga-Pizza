package com.example.saratogapizza.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_toppings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTopping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String toppingName; // Example: "Extra Range source"

    private BigDecimal extraPrice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String imageUrl;
}
