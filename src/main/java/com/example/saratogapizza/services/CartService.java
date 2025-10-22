package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.*;
import com.example.saratogapizza.exceptions.AuthException;

import com.example.saratogapizza.exceptions.ProductException;
import com.example.saratogapizza.repositories.CartRepository;
import com.example.saratogapizza.repositories.ProductRepository;
import com.example.saratogapizza.repositories.ProductSizeRepository;
import com.example.saratogapizza.repositories.UserRepository;

import com.example.saratogapizza.requests.AddToCartRequest;
import com.example.saratogapizza.responses.*;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final ProductSizeRepository productSizeRepository;


    @Transactional
    public GetCustomerCartResponse getMyActiveCart(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));

        Cart cart = cartRepository.findByUserAndCheckedOutFalse(user)
                .orElseGet(() -> {
                    log.info("No active cart found for user {}, creating new one.", user.getEmail());
                    return createNewCart(user);
                });

        GetCustomerCartResponse response = new GetCustomerCartResponse();

        Set<CartItemResponseToCart>  cartItemResponseToCarts = new HashSet<>();

        cart.getCartItems().stream().map(this::mapCartItemToResponse).forEach(cartItemResponseToCarts::add);

        response.setCartItems(cartItemResponseToCarts);

        response.setTotalItem(cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum());
        response.setTotalSellingPrice(cart.getTotalSellingPrice());

        BigDecimal totalPrice = calculateTotalPrice(cart.getCartItems());

        cart.setTotalSellingPrice(totalPrice);

        int totalQuantity = cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        cart.setTotalItem(totalQuantity);

        response.setTotalSellingPrice(cart.getTotalSellingPrice());

        response.setDiscount(cart.getDiscount() != null ? cart.getDiscount() : BigDecimal.ZERO);

        if (cart.getCoupon() != null) {
            response.setCoupon(mapCouponResponseToCart(cart.getCoupon()));
        }

        cartRepository.save(cart);

        System.out.println("****************************\nTotal price: " + cart.getTotalSellingPrice());
        System.out.println("****************************\nTotal item: " + cart.getTotalItem());

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


    @Transactional
    public AddProductInCartResponse addProductInCard(Long userId, AddToCartRequest request) {

        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new AuthException("User not found");
        }

        Cart cart = cartRepository.findByUserAndCheckedOutFalse(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCreatedAt(LocalDateTime.now());
                    newCart.setCheckedOut(false);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new ProductException("Product not found"));

        ProductSize productSize = productSizeRepository.findById(request.getSizeId()).orElseThrow(() -> new ProductException("Product Size not found"));

        Optional<CartItem> existItems = cart.getCartItems().stream()
                .filter(item -> item.getProduct().equals(product) && item.getSize().equals(productSize))
                .findFirst();

        if (existItems.isPresent()) {
            CartItem cartItem = existItems.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        }
        else{
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setSize(productSize);
            newItem.setQuantity(request.getQuantity());
            newItem.setSellingPrice(productSize.getProduct().getPrice().add(productSize.getAdditionalPrice()));
            cart.getCartItems().add(newItem);
        }

        BigDecimal total = cart.getCartItems().stream()
                .map(i -> i.getSellingPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalSellingPrice(total);
        cart.setTotalItem(cart.getCartItems().size());
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        AddProductInCartResponse addProductInCartResponse = new AddProductInCartResponse();
        addProductInCartResponse.setProductName(product.getName());
        addProductInCartResponse.setMessage("Product added successfully in Cart");
        return addProductInCartResponse;



    }

    @Transactional
    public RemoveProductInCartResponse deleteProductInCard(Long userId, Long cartItemId) {

        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new AuthException("User not found");
        }

        Cart cart = cartRepository.findByUserAndCheckedOutFalse(user).orElseThrow(() -> new ProductException("Cart not found"));

        CartItem cartItemToRemove = cart.getCartItems().stream().filter(
                filter -> filter.getId().equals(cartItemId)

        ).findFirst().orElseThrow(() -> new ProductException("Cart item not found in Cart"));

        cart.getCartItems().remove(cartItemToRemove);

        cartRepository.save(cart);

        BigDecimal totalPrice= calculateTotalPrice(cart.getCartItems());

        cart.setTotalSellingPrice(totalPrice);

        cart.setTotalItem(cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum());

        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        RemoveProductInCartResponse response = new RemoveProductInCartResponse();
        response.setMessage("Product removed successfully from cart");
        response.setProductName(cartItemToRemove.getProduct().getName());


        return response;
    }


    private BigDecimal calculateTotalPrice(Set<CartItem> cartItems) {

        BigDecimal totalPrice = cartItems.stream()
                .map(item -> {
                    BigDecimal basePrice = item.getSize().getProduct().getPrice();
                    BigDecimal extra = item.getSize().getAdditionalPrice() != null ? item.getSize().getAdditionalPrice() : BigDecimal.ZERO;
                    return basePrice.add(extra).multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalPrice;

    }
}