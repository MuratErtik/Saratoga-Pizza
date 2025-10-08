package com.example.saratogapizza.controllers;


import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.requests.AddCategoryRequest;
import com.example.saratogapizza.requests.BusinessDetailsRequest;
import com.example.saratogapizza.requests.CreateProductRequest;
import com.example.saratogapizza.responses.BusinessDetailsResponse;
import com.example.saratogapizza.responses.CategoryResponse;
import com.example.saratogapizza.responses.CreateProductResponse;
import com.example.saratogapizza.responses.GetAllProductResponse;
import com.example.saratogapizza.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final JwtUtils jwtUtils;

    private final ProductService productService;

//    @PostMapping(value = "/admin/product/add")
//    public ResponseEntity<CreateProductResponse> createProduct(
//            @RequestBody CreateProductRequest request,
//            @RequestHeader("Authorization") String jwt
//    ){
//        String token = jwt.substring(7).trim();
//        Long userId = jwtUtils.getUserIdFromToken(token);
//
//        return ResponseEntity.ok(productService.createProduct(request));
//    }

    @PostMapping(value = "/admin/product/add", consumes = {"multipart/form-data"})
    public ResponseEntity<CreateProductResponse> createProduct(
            @RequestPart(value = "business", required = true) CreateProductRequest request,
            @RequestPart(value = "logo", required = false) List<MultipartFile>  images,
            @RequestHeader("Authorization") String jwt
    ) throws IOException {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        if (images != null) {
            request.setImages(images);
        }

        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping(value = "/public/product/get-all-product")
    public ResponseEntity<List<GetAllProductResponse>> getAllProduct(){

        return ResponseEntity.ok(productService.getAllProducts());

    }

    @GetMapping("/public/product/get-product-by-filters")
    public ResponseEntity<List<GetAllProductResponse>> getAllProductByCategory(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) boolean isVegetarian,
            @RequestParam(required = false) boolean isVegan,
            @RequestParam(required = false) int spicyLevel,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) String tags){


        return ResponseEntity.ok(productService.getProductsByFilters(categoryId,
                                                                     price,
                                                                     isVegetarian,
                                                                     isVegan,
                                                                     spicyLevel,
                                                                     rating,
                                                                     tags));

    }

    @GetMapping("/public/product/get-product/{productId}")
    public ResponseEntity<GetAllProductResponse> getProductById(@PathVariable Long productId){
        return ResponseEntity.ok(productService.getProduct(productId));
    }


}









