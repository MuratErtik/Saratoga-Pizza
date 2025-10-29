package com.example.saratogapizza.responses;

import com.example.saratogapizza.entities.Category;
import com.example.saratogapizza.entities.Product;
import com.example.saratogapizza.entities.ProductSize;
import com.example.saratogapizza.entities.ProductTopping;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductSizeResponseToCartItemTopping {


    private Long id;

    private String sizeName; // S,M,L,XL

    private BigDecimal additionalPrice; // Basic Price + this = last price

    private String name;

    private BigDecimal price;
}
