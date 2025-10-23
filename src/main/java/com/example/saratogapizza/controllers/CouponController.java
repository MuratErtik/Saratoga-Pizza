package com.example.saratogapizza.controllers;

import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.requests.AddToCartRequest;
import com.example.saratogapizza.requests.CreateCouponRequest;
import com.example.saratogapizza.responses.AddProductInCartResponse;
import com.example.saratogapizza.responses.ChangeCouponActivityResponse;
import com.example.saratogapizza.responses.CreateCouponResponse;
import com.example.saratogapizza.responses.GetCouponsResponse;
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





}
//do not forget to use coupon in cart -> ,Use,Remove,Delete,Update,,Make Active or Unactive