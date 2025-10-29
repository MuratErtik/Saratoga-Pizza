package com.example.saratogapizza.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMessageResponse {

    private boolean success;

    private String message;
}
