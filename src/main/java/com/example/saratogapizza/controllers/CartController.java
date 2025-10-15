package com.example.saratogapizza.controllers;


import com.example.saratogapizza.configs.JwtUtils;


import com.example.saratogapizza.responses.GetCustomerCartResponse;
import com.example.saratogapizza.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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