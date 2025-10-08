package com.example.saratogapizza.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int stockQuantity;
    private int reservedQuantity; // in basket but did not sell yet

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDateTime lastUpdated = LocalDateTime.now();
}

