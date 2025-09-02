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

    private String toppingName; // kullanıcı seçtiği topping

    private BigDecimal price;   // ekstra fiyat

    @ManyToOne
    private CartItem cartItem;
}
