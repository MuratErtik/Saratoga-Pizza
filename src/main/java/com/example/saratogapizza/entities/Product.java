package com.example.saratogapizza.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    private BigDecimal price;

    @ElementCollection
    private List<String> images;

    private boolean available = true;

    private boolean isVegetarian = false;

    private boolean isVegan = false;

    private int spicyLevel; // 0-5 spicy level

    private Integer preparationTime; // like 30min

    private Double discount; // %10.0

    private Double rating; // average customer point

    private String allergens; // all allergens which separate with comma,

    private String tags; // examples: "New, Suggestion One"

    private boolean customizable; // does  customer able to  add/remove topics

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; // Pizza, Beverage, Desert vb.

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSize> sizes; // Small, Mid, Big and differences of prices

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductTopping> toppings; // Extra products
}
