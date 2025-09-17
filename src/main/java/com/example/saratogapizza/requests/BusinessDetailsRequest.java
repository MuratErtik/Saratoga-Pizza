package com.example.saratogapizza.requests;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


@Data
public class BusinessDetailsRequest {

    private String businessName;

    private String description;

    private String phone;

    private String email;

    private String openHours;

    private BigDecimal minimumOrderAmount;

    private BigDecimal deliveryFee;

    private BusinessAddressRequest address;

    private MultipartFile logo;
}
