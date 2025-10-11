package com.example.saratogapizza.responses;

import com.example.saratogapizza.entities.Category;
import com.example.saratogapizza.entities.ProductSize;
import com.example.saratogapizza.entities.ProductTopping;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GetAllProductResponse {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private List<String> images;

    private boolean available;

    private boolean isVegetarian;

    private boolean isVegan;

    private int spicyLevel; // 0-5 spicy level

    private Integer preparationTime; // like 30min

    private Double discount; // %10.0

    private Double rating; // average customer point

    private String allergens; // all allergens which separate with comma,

    private String tags; // examples: "New, Suggestion One"

    private boolean customizable; // does  customer able to  add/remove topics

    private String categoryName; // Pizza, Beverage, Desert vb.

    private List<ProductSizeResponse> sizes; // Small, Mid, Big and differences of prices

    private List<ProductToppingResponse> toppings; // Extra products

}


