package com.example.saratogapizza.requests;

import lombok.Data;

@Data
public class CustomerCompleteInfoRequest {

    private String  mobileNo;

    private AddressRequest  address;
}
