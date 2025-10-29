package com.example.saratogapizza.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMessageRequest {

    private Long productSizeId;

    private int quantity;

    private String action; // "RESERVE", "RELEASE", "CONFIRM"
}
