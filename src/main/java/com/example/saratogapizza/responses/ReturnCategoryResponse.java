package com.example.saratogapizza.responses;

import com.example.saratogapizza.entities.Category;

import com.example.saratogapizza.requests.ParentCategoryRequest;
import lombok.Data;

@Data
public class ReturnCategoryResponse {

    private String name;

    private String categoryId;

    private ReturnCategoryResponse parentCategory;

    private Integer level;
}
