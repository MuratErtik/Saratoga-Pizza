package com.example.saratogapizza.controllers;


import com.example.saratogapizza.configs.JwtUtils;


import com.example.saratogapizza.requests.AddToCartRequest;
import com.example.saratogapizza.responses.AddProductInCartResponse;
import com.example.saratogapizza.responses.GetCustomerCartResponse;
import com.example.saratogapizza.responses.RemoveProductInCartResponse;
import com.example.saratogapizza.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private final JwtUtils jwtUtils;


    @GetMapping(value = "/cart/me")
    public ResponseEntity<GetCustomerCartResponse> getMyCard(
            @RequestHeader("Authorization") String jwt

    ) {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        return ResponseEntity.ok(cartService.getMyActiveCart(userId));

    }

    @PostMapping(value = "/cart/add")
    public ResponseEntity<AddProductInCartResponse> addProductInCard(
            @RequestHeader("Authorization") String jwt,
            @RequestBody AddToCartRequest request

    ) {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);
        System.out.println("##########userId = " + userId);

        return ResponseEntity.ok(cartService.addProductInCard(userId,request));

    }

    @DeleteMapping(value = "/cart/remove/{cartItemId}")
    public ResponseEntity<RemoveProductInCartResponse> deleteProductInCard(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long  cartItemId

    ) {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        return ResponseEntity.ok(cartService.deleteProductInCard(userId,cartItemId));

    }

    @DeleteMapping(value = "/cart/remove/cart/")
    public ResponseEntity<RemoveProductInCartResponse> deleteCard(
            @RequestHeader("Authorization") String jwt
    ) {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        return ResponseEntity.ok(cartService.deleteCard(userId));

    }


}


    /*



also adding inventory service with rabbitMQ increase and decrease both
after the complete CartController


     */