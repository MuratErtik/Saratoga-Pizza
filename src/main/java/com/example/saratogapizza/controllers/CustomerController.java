package com.example.saratogapizza.controllers;

import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.requests.AddressRequest;
import com.example.saratogapizza.responses.*;


import com.example.saratogapizza.requests.CustomerCompleteInfoRequest;

import com.example.saratogapizza.requests.VerifyAccountRequest;
import com.example.saratogapizza.services.CustomerService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JwtUtils jwtUtils;

    private final CustomerService customerService;

    @PostMapping("/complete-register/{userId}")
    public ResponseEntity<CompleteRegisterResponse> completeRegister(
            @RequestBody CustomerCompleteInfoRequest request,
            @PathVariable Long userId,
            @RequestHeader("Authorization") String jwt) throws MessagingException {

        System.out.println("RAW HEADER = " + jwt);


        String token = jwt.substring(7).trim();
        System.out.println("EXTRACTED TOKEN = " + token);

        Long customerId = jwtUtils.getUserIdFromToken(token);

        if (customerId.equals(userId)) {
            CompleteRegisterResponse response = customerService.completeRegister(request, customerId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyAccountResponse> verifyAccount(
            @RequestBody VerifyAccountRequest request,
            @RequestHeader("Authorization") String jwt
            ) throws MessagingException {

        String token = jwt.substring(7).trim();


        Long userId = jwtUtils.getUserIdFromToken(token);


        VerifyAccountResponse response = customerService.verifyAccount(request,userId);
            return ResponseEntity.status(HttpStatus.OK).body(response);


    }

    @GetMapping("/get-customer-info")
    public ResponseEntity<GetCustomerInfoResponse> getAllInfo(@RequestHeader("Authorization") String jwt) throws MessagingException {

        String token = jwt.substring(7).trim();


        Long userId = jwtUtils.getUserIdFromToken(token);


        GetCustomerInfoResponse response = customerService.getAllInfo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);


    }

    //address CRUD starting...
    @GetMapping("/get-customer-addresses")
    public ResponseEntity<Set<GetCustomerAddressesResponse>> getAddressInfo(
            @RequestHeader("Authorization") String jwt) throws MessagingException {

        String token = jwt.substring(7).trim();


        Long userId = jwtUtils.getUserIdFromToken(token);



        Set<GetCustomerAddressesResponse> response = customerService.getAddressInfo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);


    }

    @PostMapping("/add-customer-addresses")
    public ResponseEntity<AddNewAddressResponse> addNewAddress(
            @RequestHeader("Authorization") String jwt, @RequestBody AddressRequest request
            ) throws MessagingException {

        String token = jwt.substring(7).trim();


        Long userId = jwtUtils.getUserIdFromToken(token);



        AddNewAddressResponse response = customerService.addNewAddress(userId,request);
        return ResponseEntity.status(HttpStatus.OK).body(response);


    }

    @DeleteMapping("/delete-customer-addresses/{addressId}")
    public ResponseEntity<DeleteNewAddressResponse> deleteNewAddress(
            @RequestHeader("Authorization") String jwt, @PathVariable Long addressId
    ) throws MessagingException {

        String token = jwt.substring(7).trim();


        Long userId = jwtUtils.getUserIdFromToken(token);



        DeleteNewAddressResponse response = customerService.deleteNewAddress(userId,addressId);
        return ResponseEntity.status(HttpStatus.OK).body(response);


    }

    @PutMapping("/change-customer-addresses/{addressId}")
    public ResponseEntity<ChangeNewAddressResponse> changeNewAddress(
            @RequestHeader("Authorization") String jwt, @PathVariable Long addressId,@RequestBody AddressRequest request
    ) throws MessagingException {

        String token = jwt.substring(7).trim();


        Long userId = jwtUtils.getUserIdFromToken(token);


        ChangeNewAddressResponse response = customerService.changeAddress(userId,addressId,request);
        return ResponseEntity.status(HttpStatus.OK).body(response);


    }
    //address CRUD ending...


}
