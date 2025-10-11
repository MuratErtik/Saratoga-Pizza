package com.example.saratogapizza.controllers;


import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.requests.*;
import com.example.saratogapizza.responses.*;
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

    @DeleteMapping("/public/product/delete-product/{productId}")
    public ResponseEntity<DeleteProductResponse> deleteProduct(@PathVariable Long productId){
        return ResponseEntity.ok(productService.deleteProduct(productId));
    }

    @PutMapping("/admin/product/update-product/{productId}")
    public ResponseEntity<UpdateProductResponse>  updateProduct(@PathVariable Long productId,
                                                              @RequestPart(value = "business", required = false) CreateProductRequest request,
                                                              @RequestPart(value = "logo", required = false) List<MultipartFile>  images,
                                                              @RequestHeader("Authorization") String jwt){
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        if (images != null) {
            request.setImages(images);
        }

        return ResponseEntity.ok(productService.updateProduct(request,productId));
    }

    //search product and topseller

    @GetMapping("/public/product/search-product-by-filters")
    public ResponseEntity<List<GetAllProductResponse>> searchProduct(@RequestParam String search){

        return ResponseEntity.ok(productService.searchProduct(search));


    }

    //Deal and DealItem CRUD
    @PostMapping(value = "/admin/deal/add", consumes = {"multipart/form-data"})
    public ResponseEntity<CreateDealResponse> createDeal(
            @RequestPart(value = "deal-item", required = true) CreateDealRequest request,
            @RequestPart(value = "logo", required = false) List<MultipartFile>  images,
            @RequestHeader("Authorization") String jwt
    ) throws IOException {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        if (images != null) {
            request.setImages(images);
        }

        return ResponseEntity.ok(productService.createDeal(request));
    }

    @GetMapping(value = "/public/deal/get-all-deal")
    public ResponseEntity<List<GetAllDealResponse>> getAllDeal(){

        return ResponseEntity.ok(productService.getAllDeal());

    }

    @GetMapping("/public/deal/get-deal/{dealId}")
    public ResponseEntity<GetAllDealResponse> getDealById(@PathVariable Long dealId){
        return ResponseEntity.ok(productService.getDeal(dealId));
    }

    @GetMapping("/public/deal/search-deal")
    public ResponseEntity<List<GetAllDealResponse>> searchDeal(@RequestParam String search){

        return ResponseEntity.ok(productService.searchDeal(search));


    }

    @DeleteMapping("/public/deal/delete-deal/{dealId}")
    public ResponseEntity<DeleteDealResponse> deleteDeal(@PathVariable Long dealId){
        return ResponseEntity.ok(productService.deleteDeal(dealId));
    }

    @PutMapping("/admin/deal/update-deal/{dealId}")
    public ResponseEntity<UpdateDealResponse>  updateDeal(@PathVariable Long dealId,
                                                                @RequestPart(value = "deal", required = false) CreateDealRequest request,
                                                                @RequestPart(value = "logo", required = false) List<MultipartFile>  images,
                                                                @RequestHeader("Authorization") String jwt){
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        if (images != null) {
            request.setImages(images);
        }

        return ResponseEntity.ok(productService.updateDeal(request,dealId));
    }


    @PostMapping(value = "/admin/product/size/add", consumes = {"multipart/form-data"})
    public ResponseEntity<CreateProductSizeResponse> createProductSize(
            @RequestPart(value = "product-size", required = true) CreateProductSizeRequest request,
            @RequestHeader("Authorization") String jwt
    ) throws IOException {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);


        return ResponseEntity.ok(productService.createProductSize(request));
    }

    @PutMapping(value = "/admin/product/size/update", consumes = {"multipart/form-data"})
    public ResponseEntity<UpdateProductSizeResponse> updateProductSize(
            @RequestPart(value = "product-size", required = true) UpdateProductSizeRequest request,
            @RequestHeader("Authorization") String jwt
    ) throws IOException {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);


        return ResponseEntity.ok(productService.updateProductSize(request));
    }

    @DeleteMapping("/admin/product-size/delete-size/{sizeId}")
    public ResponseEntity<DeleteProductResponse> deleteProductSize(@PathVariable Long sizeId){
        return ResponseEntity.ok(productService.deleteProductSize(sizeId));
    }

    @PostMapping(value = "/admin/product/{productId}/add/product-topping", consumes = {"multipart/form-data"})
    public ResponseEntity<CreateProductToppingResponse> createProductTopping(
            @RequestPart(value = "product-topping", required = true) CreateProductToppingRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile  image,
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwt
    ) throws IOException {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        if (image != null) {
            request.setImageUrl(image);
        }

        return ResponseEntity.ok(productService.createProductTopping(request,productId));
    }

    @PutMapping(value = "/admin/product/{productId}/update/product-topping", consumes = {"multipart/form-data"})
    public ResponseEntity<UpdateProductToppingResponse> updateProductTopping(
            @RequestPart(value = "product-topping", required = true) CreateProductToppingRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile  image,
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwt
    ) throws IOException {
        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        if (image != null) {
            request.setImageUrl(image);
        }

        return ResponseEntity.ok(productService.updateProductTopping(request,productId));
    }

    @DeleteMapping(value = "/admin/product/{productId}/delete/product-topping/{productToppingId}")
    public ResponseEntity<DeleteProductToppingResponse> deleteProductTopping(@PathVariable Long productId,@PathVariable Long productToppingId){
        return ResponseEntity.ok(productService.deleteProductTopping(productId,productToppingId));
    }



}

