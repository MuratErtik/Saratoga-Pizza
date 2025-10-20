package com.example.saratogapizza.requests;

import lombok.Data;

@Data
public class AddToCartRequest {

    private Long productId;
    private Long sizeId;
    private int quantity = 1;

}
