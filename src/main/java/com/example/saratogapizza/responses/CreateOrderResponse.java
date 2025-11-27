package com.example.saratogapizza.responses;

import com.example.saratogapizza.domains.OrderStatus;
import com.example.saratogapizza.domains.PaymentDetail;
import com.example.saratogapizza.domains.PaymentStatus;
import com.example.saratogapizza.entities.Address;
import com.example.saratogapizza.entities.Cart;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateOrderResponse {


    private Long id;

    private String message;

    private GetCustomerAddressesResponse getCustomerAddresses;

    private GetCustomerCartResponse cart;

    private PaymentDetail paymentDetail;

    private OrderStatus orderStatus;


    private PaymentStatus paymentStatus;

    private LocalDateTime orderDate ;

    private LocalDateTime deliverDate; //deliverDate = orderDate + preparationTime (min) + deliveryTime (min/h)

    private String notes;



}
