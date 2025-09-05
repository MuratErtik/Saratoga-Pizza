package com.example.saratogapizza.controllers;

import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.repsonses.AuthResponse;
import com.example.saratogapizza.repsonses.CompleteRegisterResponse;

import com.example.saratogapizza.requests.CustomerCompleteInfoRequest;

import com.example.saratogapizza.services.CustomerService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
