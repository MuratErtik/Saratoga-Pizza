package com.example.saratogapizza.entities;

import com.example.saratogapizza.domains.OrderStatus;
import com.example.saratogapizza.domains.PaymentDetail;
import com.example.saratogapizza.domains.PaymentStatus;


import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @ManyToOne
//    private User user;

//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    private Address shippingAddress;

    @OneToOne
    private Cart cart;


//    @Embedded
//    private PaymentDetail paymentDetail;


//    private BigDecimal totalSellingPrice;
//
//    private BigDecimal discount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

//    private int totalItem;

//    @Enumerated(EnumType.STRING)
//    private PaymentStatus paymentStatus= PaymentStatus.PENDING;

    private LocalDateTime orderDate = LocalDateTime.now();

    private LocalDateTime deliverDate; //deliverDate = orderDate + preparationTime (min) + deliveryTime (min/h)

    private String notes;

    private String trackingCode;

//    private LocalDateTime paymentDate;




}