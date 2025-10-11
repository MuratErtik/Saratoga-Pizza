package com.example.saratogapizza.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity; //how many product include in this deal (etc. 2 pizza 1 coke)

    @ManyToOne
    @JoinColumn(name = "deal_id")
    private Deal deal;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; //the product which include that deal
}
