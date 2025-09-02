package com.example.saratogapizza.entities;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;

    @ManyToOne(optional = true)
    private ProductSize size;

    private int quantity;

    private BigDecimal sellingPrice;

    private BigDecimal discount;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemTopping> toppings = new ArrayList<>();



}
