package com.example.saratogapizza.requests;

import com.example.saratogapizza.entities.Category;
import lombok.Data;

@Data
public class AddCategoryRequest {

    private String name;

    private String categoryId;

    private ParentCategoryRequest parentCategory;

    private Integer level;


}

