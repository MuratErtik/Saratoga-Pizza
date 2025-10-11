package com.example.saratogapizza.controllers;

import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.requests.AddCategoryRequest;

import com.example.saratogapizza.responses.CategoryResponse;
import com.example.saratogapizza.responses.ReturnCategoryResponse;
import com.example.saratogapizza.services.CategoryService;
import com.rabbitmq.client.Return;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final JwtUtils jwtUtils;

    private final CategoryService categoryService;

    //basic CRUD
    @PostMapping(value = "/admin/category/add")
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody AddCategoryRequest request,
            @RequestHeader("Authorization") String jwt
    ){
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        return ResponseEntity.ok(categoryService.createCategory(request, userId));
    }

    @PostMapping(value = "/admin/category/add-bulk")
    public ResponseEntity<List<CategoryResponse>> createCategories(
            @RequestBody List<AddCategoryRequest> requests,
            @RequestHeader("Authorization") String jwt
    ) {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        return ResponseEntity.ok(categoryService.createCategories(requests, userId));
    }

    @GetMapping(value = "public/category/get-all-category")
    public ResponseEntity<List<ReturnCategoryResponse>> getAllCategory() {

        return ResponseEntity.ok(categoryService.getAllCategory());


    }

    @DeleteMapping("/admin/category/delete/{categoryId}")
    public ResponseEntity<CategoryResponse> deleteCategory(
            @PathVariable String categoryId,
            @RequestHeader("Authorization") String jwt
    ) {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);



        CategoryResponse response = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(response);
    }











}
