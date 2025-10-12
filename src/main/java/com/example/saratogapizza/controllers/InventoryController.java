package com.example.saratogapizza.controllers;

import com.example.saratogapizza.requests.AddCategoryRequest;
import com.example.saratogapizza.requests.CreateStockRequest;
import com.example.saratogapizza.responses.CategoryResponse;
import com.example.saratogapizza.responses.CreateInventoryResponse;
import com.example.saratogapizza.responses.GetAllProductInventoryResponse;
import com.example.saratogapizza.responses.InventoryAfterBasketResponse;
import com.example.saratogapizza.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    //create a new stock save
    @PostMapping(value = "/admin/inventory/{productSizeId}/create-stock")
    public ResponseEntity<CreateInventoryResponse> createStock(
            @RequestBody CreateStockRequest request,
            @PathVariable Long productSizeId,
            @RequestHeader("Authorization") String jwt
    ){
        String token = jwt.substring(7).trim();

        return ResponseEntity.ok(inventoryService.createStock(request, productSizeId));
    }

    //stock save as manuel
    @PutMapping(value = "/admin/inventory/{productSizeId}/update-stock")
    public ResponseEntity<CreateInventoryResponse> updateStock(
            @RequestBody CreateStockRequest request,
            @PathVariable Long productSizeId,
            @RequestHeader("Authorization") String jwt
    ){
        String token = jwt.substring(7).trim();

        return ResponseEntity.ok(inventoryService.updateStock(request, productSizeId));
    }

    //add the basket
    @PostMapping(value = "/admin/inventory/{productSizeId}/add-basket")
    public ResponseEntity<InventoryAfterBasketResponse> addBasket(
            @PathVariable Long productSizeId,
            @RequestParam int quantity,
            @RequestHeader("Authorization") String jwt
    ){
        String token = jwt.substring(7).trim();

        return ResponseEntity.ok(inventoryService.addBasketUpdate(productSizeId,quantity));
    }


    //remove the basket

    @DeleteMapping("/admin/inventory/{productSizeId}/remove-basket")
    public ResponseEntity<InventoryAfterBasketResponse> removeBasket(
            @PathVariable Long productSizeId,
            @RequestParam int quantity,
            @RequestHeader("Authorization") String jwt
    ) {
        String token = jwt.substring(7).trim();

        InventoryAfterBasketResponse response = inventoryService.removeFromBasketUpdate(productSizeId, quantity);
        return ResponseEntity.ok(response);
    }

    //after the complete order
    @PutMapping("/admin/inventory/{productSizeId}/confirm-order")
    public ResponseEntity<InventoryAfterBasketResponse> confirmOrder(
            @PathVariable Long productSizeId,
            @RequestParam int quantity,
            @RequestHeader("Authorization") String jwt
    ) {
        String token = jwt.substring(7).trim();
        return ResponseEntity.ok(inventoryService.confirmOrderStockUpdate(productSizeId, quantity));
    }

    //list all stock
    @GetMapping(value = "/admin/inventory/{productSizeId}/list")
    public ResponseEntity<GetAllProductInventoryResponse> listStock(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long productSizeId
    ){
        String token = jwt.substring(7).trim();

        return ResponseEntity.ok(inventoryService.listStock(productSizeId));
    }


    @GetMapping(value = "/admin/inventory/list-all")
    public ResponseEntity<List<GetAllProductInventoryResponse>> listAllStock(
            @RequestHeader("Authorization") String jwt
    ){
        String token = jwt.substring(7).trim();

        return ResponseEntity.ok(inventoryService.listAllStock());
    }

}
