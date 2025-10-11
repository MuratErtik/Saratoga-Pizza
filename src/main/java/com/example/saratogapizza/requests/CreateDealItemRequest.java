package com.example.saratogapizza.requests;


import lombok.Data;

@Data
public class CreateDealItemRequest {

    private Integer quantity;

    private String title; // to access deal

    private String productName; // to access product

}
