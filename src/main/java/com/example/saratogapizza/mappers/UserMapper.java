package com.example.saratogapizza.mappers;

import com.example.saratogapizza.entities.User;
import com.example.saratogapizza.responses.GetCustomerAddressesResponse;
import com.example.saratogapizza.responses.GetCustomerInfoResponse;
import com.example.saratogapizza.services.CustomerService;

import java.util.Set;


public class UserMapper {

    private final CustomerService customerService;

    public UserMapper(CustomerService customerService) {
        this.customerService = customerService;
    }

    public  GetCustomerInfoResponse mapToGetCustomerInfoResponse(User user) {

        GetCustomerInfoResponse response = new GetCustomerInfoResponse();

        response.setName(user.getName());

        response.setEmail(user.getEmail());

        response.setMobileNo(user.getMobileNo());

        Set<GetCustomerAddressesResponse> addresses = customerService.getAddressInfo(user.getUserId());

        response.setAddresses(addresses);

        response.setLastname(user.getLastname());

        response.setVerified(user.isVerified());

        response.setCreatedAt(user.getCreatedAt());

        response.setLastLoginAt(user.getLastLoginAt());

//        response.setBankDetails();

//        response.setOrders();
//
//        response.setReviews();

        response.setUsedCoupons(user.getUsedCoupons());

        response.setLoyaltyPoints(user.getLoyaltyPoints());

        return response;
    }

}



