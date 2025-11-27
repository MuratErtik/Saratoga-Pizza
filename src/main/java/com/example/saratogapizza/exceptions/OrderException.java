package com.example.saratogapizza.exceptions;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }
}
