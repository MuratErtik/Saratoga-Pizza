package com.example.saratogapizza.responses;


import lombok.Data;

import java.math.BigDecimal;


@Data
public class BusinessDetailsResponse {

    private Long id;

    private String businessName;

    private String description;

    private String phone;

    private String email;

    private String websiteUrl;

    private String logoUrl;

    private String openHours;

    private BigDecimal minimumOrderAmount;

    private BigDecimal deliveryFee;

    private boolean active = true;

    private BusinessAddressResponse businessAddressResponse;
}
