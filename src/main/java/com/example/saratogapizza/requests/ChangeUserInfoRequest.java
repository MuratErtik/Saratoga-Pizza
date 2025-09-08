package com.example.saratogapizza.requests;

import com.example.saratogapizza.entities.Coupon;
import com.example.saratogapizza.entities.Order;
import com.example.saratogapizza.entities.Review;
import com.example.saratogapizza.responses.GetCustomerAddressesResponse;
import com.example.saratogapizza.responses.GetCustomerBankDetailsResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class ChangeUserInfoRequest {

    private String name;

    private String lastname;

    private String email; //change user status about verification

    private String mobileNo;

//    private Set<Review> reviews = new HashSet<>();
    //implement later

}
