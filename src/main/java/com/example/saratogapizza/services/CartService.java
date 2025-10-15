package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.*;
import com.example.saratogapizza.exceptions.AuthException;

import com.example.saratogapizza.repositories.CartRepository;
import com.example.saratogapizza.repositories.UserRepository;

import com.example.saratogapizza.responses.*;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final UserRepository userRepository;


    public GetCustomerCartResponse getMyActiveCart(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));

        Cart cart = cartRepository.findByUserAndCheckedOutFalse(user).orElseGet(() -> createNewCart(user));

        GetCustomerCartResponse response = new GetCustomerCartResponse();

        Set<CartItemResponseToCart>  cartItemResponseToCarts = new HashSet<>();

        cart.getCartItems().stream().map(this::mapCartItemToResponse).forEach(cartItemResponseToCarts::add);

        response.setCartItems(cartItemResponseToCarts);

        cart.setTotalItem(cart.getCartItems().size());

        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(item -> item.getSellingPrice() != null ? item.getSellingPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalSellingPrice(totalPrice);

        response.setDiscount(cart.getDiscount() != null ? cart.getDiscount() : BigDecimal.ZERO);


        if (cart.getCoupon() != null) {
            response.setCoupon(mapCouponResponseToCart(cart.getCoupon()));
        }


        return response;
    }


    private Cart createNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalSellingPrice(BigDecimal.ZERO);
        cart.setTotalItem(0);
        cart.setDiscount(BigDecimal.ZERO);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cart.setCheckedOut(false);
        return cartRepository.save(cart);
    }


    private CouponResponseToCart mapCouponResponseToCart(Coupon coupon) {

        CouponResponseToCart couponResponseToCart = new CouponResponseToCart();
        couponResponseToCart.setId(coupon.getId());
        couponResponseToCart.setCode(coupon.getCode());
        couponResponseToCart.setValidityStartDate(coupon.getValidityStartDate());
        couponResponseToCart.setValidityEndDate(coupon.getValidityEndDate());
        couponResponseToCart.setMinOrderValue(coupon.getMinOrderValue());
        return couponResponseToCart;
    }

    private CartItemResponseToCart mapCartItemToResponse(CartItem cartItem) {

        CartItemResponseToCart response = new CartItemResponseToCart();
        response.setId(cartItem.getId());
        ProductResponseToCartItem cartItemResponseToCartItem = mapProductResponseToCartItem(cartItem.getSize());
        response.setProductSize(cartItemResponseToCartItem);
        response.setQuantity(cartItem.getQuantity());

        List<CartItemToppingResponseToCart> cartItemToppingResponseToCarts = new ArrayList<>();

        if (cartItem.getExtraToppings() != null) {
            cartItem.getExtraToppings().stream()
                    .map(this::mapCartItemToppingResponseToCart)
                    .forEach(cartItemToppingResponseToCarts::add);
        }

        response.setExtraToppings(cartItemToppingResponseToCarts);
        response.setSellingPrice(cartItem.getSellingPrice());
        return response;

    }

    private ProductResponseToCartItem mapProductResponseToCartItem(ProductSize productSize){

        ProductResponseToCartItem response = new ProductResponseToCartItem();
        response.setId(productSize.getId());
        response.setSizeName(productSize.getSizeName());
        response.setAdditionalPrice(productSize.getAdditionalPrice());
        if (productSize.getProduct() != null) {
            response.setName(productSize.getProduct().getName());
            response.setPrice(productSize.getProduct().getPrice());
        }

        return response;

    }

    private CartItemToppingResponseToCart mapCartItemToppingResponseToCart(CartItemTopping cartItemTopping){

        CartItemToppingResponseToCart response = new CartItemToppingResponseToCart();

        response.setId(cartItemTopping.getId());

        // it was deprecated because of risk of recursive loop!

        //ProductSizeResponseToCartItemTopping productSizeResponseToCartItemTopping = mapProductSizeResponseToCartItemTopping(cartItemTopping.getCartItem().getSize());

        //response.setProductSize(productSizeResponseToCartItemTopping);

        response.setQuantity(cartItemTopping.getCartItem().getQuantity());

        response.setSellingPrice(cartItemTopping.getCartItem().getSellingPrice());

        //List<CartItemToppingResponse> cartItemToppingResponses = new ArrayList<>();

        //cartItemTopping.getCartItem().getExtraToppings().stream().map(this::mapCartItemToppingResponse).forEach(cartItemToppingResponses::add);

        //response.setExtraToppings(cartItemToppingResponses);

        return response;

    }

    private CartItemToppingResponse mapCartItemToppingResponse(CartItemTopping cartItemTopping){
        CartItemToppingResponse cartItemToppingResponse = new CartItemToppingResponse();
        cartItemToppingResponse.setId(cartItemTopping.getId());
        cartItemToppingResponse.setToppingName(cartItemTopping.getToppingName());
        cartItemToppingResponse.setPrice(cartItemTopping.getPrice());
        return cartItemToppingResponse;

    }


    private ProductSizeResponseToCartItemTopping mapProductSizeResponseToCartItemTopping(ProductSize productSize){
        ProductSizeResponseToCartItemTopping response = new ProductSizeResponseToCartItemTopping();
        response.setId(productSize.getId());
        response.setSizeName(productSize.getSizeName());
        response.setAdditionalPrice(productSize.getAdditionalPrice());
        if (productSize.getProduct() != null) {
            response.setName(productSize.getProduct().getName());
            response.setPrice(productSize.getProduct().getPrice());
        }

        return response;
    }
}