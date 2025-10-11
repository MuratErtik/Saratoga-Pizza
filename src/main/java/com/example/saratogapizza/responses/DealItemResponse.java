package com.example.saratogapizza.responses;

import com.example.saratogapizza.entities.Deal;
import com.example.saratogapizza.entities.Product;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class DealItemResponse {

    private Long id;

    private Integer quantity; //how many product include in this deal (etc. 2 pizza 1 coke)

    private String productName; //the product which include that deal
}
