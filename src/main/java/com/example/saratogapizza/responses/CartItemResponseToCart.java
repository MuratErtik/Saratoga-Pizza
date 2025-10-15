package com.example.saratogapizza.responses;

import com.example.saratogapizza.entities.Cart;
import com.example.saratogapizza.entities.CartItemTopping;
import com.example.saratogapizza.entities.Product;
import com.example.saratogapizza.entities.ProductSize;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItemResponseToCart {


    private Long id;

    //private Cart cart;

    private ProductResponseToCartItem productSize; // we going to get all product info in here

    private int quantity = 1;

    private BigDecimal sellingPrice;

    private List<CartItemToppingResponseToCart> extraToppings;
}
