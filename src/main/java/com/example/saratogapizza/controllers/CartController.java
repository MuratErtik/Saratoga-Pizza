package com.example.saratogapizza.controllers;


import com.example.saratogapizza.configs.JwtUtils;


import com.example.saratogapizza.requests.AddToCartRequest;
import com.example.saratogapizza.responses.AddProductInCartResponse;
import com.example.saratogapizza.responses.GetCustomerCartResponse;
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


    @GetMapping(value = "/card/me")
    public ResponseEntity<GetCustomerCartResponse> getMyCard(
            @RequestHeader("Authorization") String jwt

    ) {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        return ResponseEntity.ok(cartService.getMyActiveCart(userId));

    }

    @PostMapping(value = "/card/add")
    public ResponseEntity<AddProductInCartResponse> addProductInCard(
            @RequestHeader("Authorization") String jwt,
            @RequestBody AddToCartRequest request

    ) {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);
        System.out.println("##########userId = " + userId);

        return ResponseEntity.ok(cartService.addProductInCard(userId,request));

    }


}


    /*
GET /api/cart/me
Kullanıcının aktif sepetini getirir.
Eğer yoksa boş döner veya yeni oluşturur.

POST /api/cart/add
Sepete ürün ekler (varsa miktar artırır).

 PUT /api/cart/update/{cartItemId}
Sepetteki ürün miktarını değiştirir.

 DELETE /api/cart/remove/{cartItemId}
Sepetten ürünü kaldırır.
DELETE /api/cart/clear
Sepeti tamamen boşaltır.
     */