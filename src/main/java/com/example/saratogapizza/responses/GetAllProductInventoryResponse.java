package com.example.saratogapizza.responses;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetAllProductInventoryResponse{

    private Long id;

    private int stockQuantity;

    private int reservedQuantity; // in basket but did not sell yet

    private String sizeName; // S,M,L,XL

    private BigDecimal additionalPrice; // Basic Price + this = last price

    private String name;

    private BigDecimal price;

    private boolean available = true;

    private String categoryName; // Pizza, Beverage, Desert vb.

    private LocalDateTime lastUpdated;
}
