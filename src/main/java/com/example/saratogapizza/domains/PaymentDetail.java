package com.example.saratogapizza.domains;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class PaymentDetail {
    private String paymentMethod;   // e.g. CREDIT_CARD, CASH
    private String transactionId;   // reference code of system of payment
    private BigDecimal amountPaid;
}

