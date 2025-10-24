package com.example.saratogapizza.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemTopping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String toppingName; // topping name which added in basket by user

    private BigDecimal price;   // extra price

    @ManyToOne
    private CartItem cartItem;
}
