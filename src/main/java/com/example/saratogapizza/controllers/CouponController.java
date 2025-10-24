package com.example.saratogapizza.controllers;

import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.requests.AddToCartRequest;
import com.example.saratogapizza.requests.CreateCouponRequest;
import com.example.saratogapizza.responses.*;
import com.example.saratogapizza.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CouponController {

    private final CartService cartService;

    private final JwtUtils jwtUtils;

    @PostMapping(value = "/admin/coupon/create")
    public ResponseEntity<CreateCouponResponse> createCoupon(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CreateCouponRequest request

    ) {
        String token = jwt.substring(7).trim();
//        Long userId = jwtUtils.getUserIdFromToken(token);

        return ResponseEntity.ok(cartService.createCoupon(request));

    }

    @GetMapping(value = "/admin/coupon/get-coupons")
    public ResponseEntity<List<GetCouponsResponse>> getCoupons(
            @RequestHeader("Authorization") String jwt
    ) {
        String token = jwt.substring(7).trim();
//        Long userId = jwtUtils.getUserIdFromToken(token);

        return ResponseEntity.ok(cartService.getCoupons());

    }


    @PutMapping(value = "/admin/coupon/change-activity/{couponId}")
    public ResponseEntity<ChangeCouponActivityResponse> changeActivityOfCoupons(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long couponId
    ) {
        String token = jwt.substring(7).trim();

        return ResponseEntity.ok(cartService.changeActivityOfCoupons(couponId));

    }

    @PatchMapping(value = "/admin/coupon/update/{couponId}")
    public ResponseEntity<UpdateCouponResponse> updateCoupon(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CreateCouponRequest request,
            @PathVariable Long couponId

    ) {
        String token = jwt.substring(7).trim();
//        Long userId = jwtUtils.getUserIdFromToken(token);

        return ResponseEntity.ok(cartService.updateCoupon(couponId,request));

    }

    //make control of date if date is before the starting time or after the end time make coupon activity false
    @PatchMapping(value = "/admin/coupon/delete/{couponId}")
    public ResponseEntity<ChangeCouponActivityResponse> unactiveCoupon(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long couponId
    ) {
        String token = jwt.substring(7).trim();

        return ResponseEntity.ok(cartService.unactiveCoupon(couponId));

    }

    @PatchMapping(value = "customer/coupon/apply")
    public ResponseEntity<ApplyCouponResponse> applyCouponToCart(
            @RequestHeader("Authorization") String jwt,
            @RequestParam String couponCode){

        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        return ResponseEntity.ok(cartService.applyCouponToCart(userId, couponCode));
    }






}
//do not forget to use coupon in cart -> ,Use,Remove,,,,