package com.example.saratogapizza.responses;

import lombok.Data;

@Data
public class CreateInventoryResponse {
    private String message;
    private String productName;
    private int quantity;
}
