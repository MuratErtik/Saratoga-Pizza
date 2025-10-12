package com.example.saratogapizza.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryAfterBasketResponse {
    private String message;
    private String productName;
    private int requestedQuantity;
    private int reservedQuantity;
    private int availableStock;
    private LocalDateTime lastUpdated;
}
