package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.*;
import com.example.saratogapizza.exceptions.AuthException;

import com.example.saratogapizza.exceptions.ProductException;
import com.example.saratogapizza.repositories.*;

import com.example.saratogapizza.requests.AddToCartRequest;
import com.example.saratogapizza.requests.CreateCouponRequest;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final ProductSizeRepository productSizeRepository;

    private final CouponRepository couponRepository;


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

        Cart cart = cartRepository.findByUserAndCheckedOutFalse(user)
                .orElseThrow(() -> new ProductException("Cart not found"));

        CartItem cartItemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ProductException("Cart item not found in cart"));

        if (cartItemToRemove.getQuantity() > 1) {
            cartItemToRemove.setQuantity(cartItemToRemove.getQuantity() - 1);
        } else {
            cart.getCartItems().remove(cartItemToRemove);
        }

        // cart.getCartItems().remove(cartItemToRemove);

        BigDecimal totalPrice = calculateTotalPrice(cart.getCartItems());
        cart.setTotalSellingPrice(totalPrice);

        int totalItems = cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        cart.setTotalItem(totalItems);

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

    @Transactional
    public RemoveProductInCartResponse deleteCard(Long userId) {
        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new AuthException("User not found");
        }

        Cart cart = cartRepository.findByUserAndCheckedOutFalse(user)
                .orElseThrow(() -> new ProductException("Cart not found"));

        if (cart.isCheckedOut()) {
            throw new ProductException("Cannot clear a checked-out cart");
        }


        cart.getCartItems().clear();

        cart.setCoupon(null);
        cart.setDiscount(BigDecimal.ZERO);
        cart.setTotalSellingPrice(BigDecimal.ZERO);
        cart.setTotalItem(0);
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        RemoveProductInCartResponse response = new RemoveProductInCartResponse();
        response.setMessage("Cart cleared successfully");

        return response;
    }

    @Transactional
    public CreateCouponResponse createCoupon(CreateCouponRequest request) {

        validateCoupon(request);

        Optional<Coupon> existingCoupon = couponRepository.findByCode(request.getCode());

        if (existingCoupon.isPresent()) {
            throw new ProductException("Coupon already exists");
        }

        Coupon coupon = new Coupon();

        coupon.setCode(request.getCode());

        coupon.setDiscountPercentage(request.getDiscountPercentage());

        coupon.setValidityStartDate(request.getValidityStartDate());

        coupon.setValidityEndDate(request.getValidityEndDate());

        coupon.setMinOrderValue(request.getMinOrderValue());

        coupon.setActive(true);

        couponRepository.save(coupon);

        CreateCouponResponse response = new CreateCouponResponse();
        response.setMessage("Coupon created successfully");
        response.setCode(coupon.getCode());

        return response;
    }

    private void validateCoupon(CreateCouponRequest request) {

        if (request.getCode() == null || request.getCode().isEmpty()) {
            throw new ProductException("Coupon code cannot be empty");
        }

        if (request.getDiscountPercentage() <= 0 || request.getDiscountPercentage() > 100) {
            throw new ProductException("Discount percentage must be between 0 and 100");
        }

        if (request.getValidityStartDate() == null || request.getValidityEndDate() == null) {
            throw new ProductException("Coupon validity dates cannot be null");
        }

        if (request.getValidityEndDate().isBefore(request.getValidityStartDate())) {
            throw new ProductException("End date cannot be before start date");
        }

        if (request.getMinOrderValue() <0 ) {
            throw new ProductException("Coupon value must be greater than 0");
        }
    }

    public List<GetCouponsResponse> getCoupons() {

        return couponRepository.findAll().stream().map(this::mapToGetCouponsResponse).collect(Collectors.toList());
    }

    private GetCouponsResponse mapToGetCouponsResponse(Coupon coupon) {
        GetCouponsResponse response = new GetCouponsResponse();
        response.setId(coupon.getId());
        response.setCode(coupon.getCode());
        response.setDiscountPercentage(coupon.getDiscountPercentage());
        response.setValidityStartDate(coupon.getValidityStartDate());
        response.setValidityEndDate(coupon.getValidityEndDate());
        response.setMinOrderValue(coupon.getMinOrderValue());
        response.setActive(coupon.isActive());
        Set<UserToUseThisCouponResponse> users = new HashSet<>();
        coupon.getUsedByUsers().stream().map(this::mapToUserToUseThisCouponResponse).forEach(users::add);
        response.setUsedByUsers(users);
        return response;
    }

    private UserToUseThisCouponResponse mapToUserToUseThisCouponResponse(User user) {
        UserToUseThisCouponResponse response = new UserToUseThisCouponResponse();
        response.setFullName(user.getName()+" "+user.getLastname());
        //add order details later!
        return response;

    }


    @Transactional
    public ChangeCouponActivityResponse changeActivityOfCoupons(Long couponId) {

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new ProductException("Coupon does not exist with id: " + couponId));

        coupon.setActive(!coupon.isActive());

        couponRepository.save(coupon);

        ChangeCouponActivityResponse response = new ChangeCouponActivityResponse();
        response.setMessage("Coupon activated has been changed");
        return response;

    }

    @Transactional
    public UpdateCouponResponse updateCoupon(Long couponId, CreateCouponRequest request) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ProductException("Coupon not found"));

        if (request.getValidityStartDate() != null && request.getValidityEndDate() != null) {
            if (request.getValidityEndDate().isBefore(request.getValidityStartDate())) {
                throw new ProductException("End date cannot be before start date");
            }
        }


        if (request.getDiscountPercentage() < 0 || request.getDiscountPercentage() > 100) {
            throw new ProductException("Discount percentage must be between 0 and 100");
        }

        coupon.setDiscountPercentage(request.getDiscountPercentage());


        if (request.getCode() != null && !request.getCode().isBlank()) {
            coupon.setCode(request.getCode());
        }

        if (request.getValidityStartDate() != null) {
            coupon.setValidityStartDate(request.getValidityStartDate());
        }

        if (request.getValidityEndDate() != null) {
            coupon.setValidityEndDate(request.getValidityEndDate());
        }


        coupon.setMinOrderValue(request.getMinOrderValue());


        couponRepository.save(coupon);

        UpdateCouponResponse response = new UpdateCouponResponse();
        response.setMessage("Coupon updated successfully");

        return response;
    }


    public ChangeCouponActivityResponse unactiveCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ProductException("Coupon not found with id: " + couponId));

        LocalDate today = LocalDate.now();

        if (today.isBefore(coupon.getValidityStartDate()) || today.isAfter(coupon.getValidityEndDate())) {
            coupon.setActive(false);
        } else {
            throw new ProductException("Coupon is still within valid date range, cannot deactivate based on date!");
        }

        couponRepository.save(coupon);

        ChangeCouponActivityResponse response = new ChangeCouponActivityResponse();
        response.setMessage("Coupon unactivated successfully");
        return response;

    }

}