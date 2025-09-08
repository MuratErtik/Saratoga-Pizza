package com.example.saratogapizza.controllers;

import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.requests.*;
import com.example.saratogapizza.responses.*;


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

    //bankDetails CRUD starting...
    @GetMapping("/get-customer-bank-details")
    public ResponseEntity<Set<GetCustomerBankDetailsResponse>> getBankDetailsInfo(
            @RequestHeader("Authorization") String jwt){

        String token = jwt.substring(7).trim();


        Long userId = jwtUtils.getUserIdFromToken(token);



        Set<GetCustomerBankDetailsResponse> response = customerService.getBankDetailsInfo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);


    }


    @PostMapping("/add-customer-bank-details")
    public ResponseEntity<AddNewBankDetailsResponse> addNewBankDetail(
            @RequestHeader("Authorization") String jwt, @RequestBody BankDetailsRequest request
    ) throws MessagingException {

        String token = jwt.substring(7).trim();


        Long userId = jwtUtils.getUserIdFromToken(token);



        AddNewBankDetailsResponse response = customerService.addNewBankDetail(userId,request);
        return ResponseEntity.status(HttpStatus.OK).body(response);


    }


    @DeleteMapping("/delete-customer-bank-details/{bankDetailsId}")
    public ResponseEntity<DeleteBankDetailsResponse> deleteBankDetails(
            @RequestHeader("Authorization") String jwt, @PathVariable Long bankDetailsId
    ) throws MessagingException {

        String token = jwt.substring(7).trim();


        Long userId = jwtUtils.getUserIdFromToken(token);



        DeleteBankDetailsResponse response = customerService.deleteBankDetails(userId,bankDetailsId);
        return ResponseEntity.status(HttpStatus.OK).body(response);


    }

    @PutMapping("/change-customer-bank-details/{bankDetailsId}")
    public ResponseEntity<ChangeBankDetailsResponse> changeBankDetails(
            @RequestHeader("Authorization") String jwt, @PathVariable Long bankDetailsId,@RequestBody BankDetailsRequest request
    ) throws MessagingException {

        String token = jwt.substring(7).trim();


        Long userId = jwtUtils.getUserIdFromToken(token);


        ChangeBankDetailsResponse response = customerService.changeBankDetails(userId,bankDetailsId,request);
        return ResponseEntity.status(HttpStatus.OK).body(response);


    }
    //bankDetails CRUD ending...


    @PutMapping("/change-customer-info")
    public ResponseEntity<ChangeUserInfoResponse> changeBankDetails(
            @RequestHeader("Authorization") String jwt,@RequestBody ChangeUserInfoRequest request
    ) throws MessagingException {

        String token = jwt.substring(7).trim();

        Long customerId = jwtUtils.getUserIdFromToken(token);

        ChangeUserInfoResponse response = customerService.changeOtherDetails(customerId,request);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }











}
