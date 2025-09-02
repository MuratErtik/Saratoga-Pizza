package com.example.saratogapizza.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item_topping")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class OrderItemTopping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private OrderItem orderItem;

    @ManyToOne
    private ProductTopping topping;

    private BigDecimal price; // Topping price

    private int quantity; // if 2x extra topics
}
