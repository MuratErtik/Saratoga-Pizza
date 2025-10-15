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
public class CartItemToppingResponseToCart {

    private Long id;



    //Recursive!!!

    //private ProductSizeResponseToCartItemTopping productSize;

    private int quantity = 1;

    private BigDecimal sellingPrice;

    //private List<CartItemToppingResponse> extraToppings;

}
