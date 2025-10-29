package com.example.saratogapizza.responses;

import com.example.saratogapizza.entities.CartItem;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemToppingResponse {


    private Long id;

    private String toppingName;

    private BigDecimal price;


}
