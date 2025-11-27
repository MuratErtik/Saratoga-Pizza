package com.example.saratogapizza.requests;


import lombok.Data;


@Data
public class CreateOrderRequest {


    private Long shippingAddressId;

    private String notes;


}
