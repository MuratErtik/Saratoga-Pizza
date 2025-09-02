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

    private int stockQuantity; // mevcut stok adedi
    private int reservedQuantity; // sepete eklenmiş ama ödenmemiş miktar

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDateTime lastUpdated = LocalDateTime.now();
}

