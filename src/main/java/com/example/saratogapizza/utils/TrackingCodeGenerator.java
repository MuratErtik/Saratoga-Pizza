package com.example.saratogapizza.utils;

import com.example.saratogapizza.entities.Order;
import java.util.Random;

public class TrackingCodeGenerator {

    public static String generateCode(Order order) {

        if (order == null || order.getId() == null ||
                order.getCart() == null || order.getCart().getUser() == null) {
            throw new IllegalArgumentException("Order or user information is missing");
        }

        Random random = new Random();

        int rand = 1000 + random.nextInt(9000); // optional

        return "ORD_" + order.getId() + "_" + order.getCart().getUser().getUserId() + "_" + rand;
    }
}
