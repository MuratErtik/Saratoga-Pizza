package com.example.saratogapizza.controllers;

import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.services.CartService;
import com.example.saratogapizza.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final JwtUtils jwtUtils;




}
// customer able to create an order.  -> RabbitMQ’ya order.created event’i atılır (stok azaltsın, mail gitsin vs).
// customer will be able to olds orders or by filtering from past
// admin able to list all or filtering orders
//admin able to change of order state
//notification server must be implemented->
//for customer: your order has been completed. , change of order status
//for admin: got a new order , cancellation from customer

/*
| Event             | Publisher    | Consumer                                 |
| ----------------- | ------------ | ---------------------------------------- |
| `order.created`   | OrderService | InventoryService, EmailService           |
| `order.canceled`  | OrderService | InventoryService (stok iade)             |
| `order.delivered` | OrderService | AnalyticsService (istatistik güncelleme) |

 */