package com.example.saratogapizza.requests;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;


@Data
public class CreateProductRequest {

    private String name;

    private String description;

    private BigDecimal price;

    private List<MultipartFile> images;

    private boolean isVegetarian = false;

    private boolean isVegan = false;

    private int spicyLevel; // 0-5 spicy level

    private Integer preparationTime; // like 30min

    private String allergens; // all allergens which separate with comma,

    private String categoryId;

    private String tags; // examples: "New, Suggestion One"

    private boolean customizable; // does  customer able to  add/remove topic

}
