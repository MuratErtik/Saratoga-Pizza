package com.example.saratogapizza.responses;

import com.example.saratogapizza.entities.Product;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductToppingResponse {

    private Long id;

    private String toppingName; // Example: "Extra Range source"

    private BigDecimal extraPrice;

    private String imageUrl;
}
