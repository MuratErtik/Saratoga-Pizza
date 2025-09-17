package com.example.saratogapizza.controllers;

import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.requests.BusinessDetailsRequest;
import com.example.saratogapizza.responses.BusinessDetailsResponse;
import com.example.saratogapizza.services.BusinessDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/business")
@RequiredArgsConstructor
public class BusinessDetailsController {

    private final BusinessDetailsService businessDetailsService;

    private final JwtUtils jwtUtils;

    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    public ResponseEntity<BusinessDetailsResponse> createBusiness(
            @RequestPart(value = "business", required = true) BusinessDetailsRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestHeader("Authorization") String jwt
    ) throws IOException {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        if (logo != null) {
            request.setLogo(logo);
        }

        return ResponseEntity.ok(businessDetailsService.createBusiness(request, userId));
    }

    @PutMapping(value = "/update/{businessId}", consumes = {"multipart/form-data"})
    public ResponseEntity<BusinessDetailsResponse> updateBusiness(
            @PathVariable("businessId") Long businessId,
            @RequestPart(value = "business", required = true) BusinessDetailsRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestHeader("Authorization") String jwt
    ) throws IOException {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        if (logo != null) {
            request.setLogo(logo);
        }

        return ResponseEntity.ok(businessDetailsService.updateBusiness(businessId, request, userId));
    }

    @DeleteMapping("/delete/{businessId}")
    public ResponseEntity<String> deleteBusiness(
            @PathVariable Long businessId,
            @RequestHeader("Authorization") String jwt
    ) throws IOException {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        businessDetailsService.deleteBusiness(businessId, userId);

        return ResponseEntity.ok("Business deleted successfully");
    }




}
