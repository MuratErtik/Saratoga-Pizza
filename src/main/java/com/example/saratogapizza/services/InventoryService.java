package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.Inventory;
import com.example.saratogapizza.entities.Product;
import com.example.saratogapizza.entities.ProductSize;
import com.example.saratogapizza.exceptions.InventoryException;
import com.example.saratogapizza.exceptions.ProductException;
import com.example.saratogapizza.repositories.InventoryRepository;
import com.example.saratogapizza.repositories.ProductRepository;
import com.example.saratogapizza.repositories.ProductSizeRepository;
import com.example.saratogapizza.requests.CreateStockRequest;
import com.example.saratogapizza.responses.CreateInventoryResponse;
import com.example.saratogapizza.responses.GetAllProductInventoryResponse;
import com.example.saratogapizza.responses.InventoryAfterBasketResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    private final ProductRepository productRepository;

    private final ProductSizeRepository productSizeRepository;


    public CreateInventoryResponse createStock(CreateStockRequest request, Long productSizeId) {

        ProductSize productSize = productSizeRepository.findById(productSizeId).orElseThrow(() -> new ProductException("Product size not found with id: " + productSizeId));

        validateStock(request,productSize);

        Inventory inventory = new Inventory();

        inventory.setProductSize(productSize);

        inventory.setStockQuantity(request.getStockQuantity());

        inventory.setLastUpdated(LocalDateTime.now());

        inventoryRepository.save(inventory);

        CreateInventoryResponse response = new CreateInventoryResponse();
        response.setMessage("Successfully created inventory");
        response.setQuantity(request.getStockQuantity());
        response.setProductName(
                inventory.getProductSize().getProduct().getName() + " - " + inventory.getProductSize().getSizeName()
        );
        return response;

    }

    private void validateStock(CreateStockRequest request, ProductSize productSize) {

        Optional<Inventory> existingInventory = inventoryRepository.findByProductSize(productSize);
        if (existingInventory.isPresent()) {
            throw new ProductException("Stock already exists for this product size");
        }

        Product product = productSize.getProduct();

        if (product==null) throw new ProductException("Product not found");

        if (request.getStockQuantity()<0) {
            throw new ProductException("Stock quantity cannot be negative");
        }

    }

    public List<GetAllProductInventoryResponse> listAllStock() {

        return inventoryRepository.findAll().stream().map(this::mapToGetAllProductInventoryResponse).collect(Collectors.toList());

    }

    private GetAllProductInventoryResponse mapToGetAllProductInventoryResponse(Inventory inventory) {
        GetAllProductInventoryResponse response = new GetAllProductInventoryResponse();
        response.setStockQuantity(inventory.getStockQuantity());
        response.setId(inventory.getId());
        response.setReservedQuantity(inventory.getReservedQuantity());
        response.setSizeName(inventory.getProductSize().getSizeName());
        response.setAdditionalPrice(inventory.getProductSize().getAdditionalPrice());
        response.setName(inventory.getProductSize().getProduct().getName());
        response.setPrice(inventory.getProductSize().getProduct().getPrice());
        response.setAvailable(inventory.getProductSize().getProduct().isAvailable());
        response.setCategoryName(inventory.getProductSize().getProduct().getCategory().getName());
        response.setLastUpdated(inventory.getLastUpdated());
        return response;

    }

    public GetAllProductInventoryResponse listStock(Long productSizeId) {

        ProductSize productSize = productSizeRepository.findById(productSizeId).orElseThrow(() -> new ProductException("Product size not found with id: " + productSizeId));

        Inventory inventory = inventoryRepository.findByProductSize(productSize).orElseThrow(() -> new ProductException("Product not found with id: " + productSizeId));

        return mapToGetAllProductInventoryResponse(inventory);

    }

    @Transactional
    public CreateInventoryResponse updateStock(CreateStockRequest request, Long productSizeId) {

        ProductSize productSize = productSizeRepository.findById(productSizeId)
                .orElseThrow(() -> new ProductException("Product size not found with id: " + productSizeId));

        Product product = productSize.getProduct();
        if (product == null) {
            throw new ProductException("Product not found for product size id: " + productSizeId);
        }

        if (request.getStockQuantity() < 0) {
            throw new ProductException("Stock quantity cannot be negative");
        }

        Inventory inventory = inventoryRepository.findByProductSize(productSize)
                .orElseThrow(() -> new ProductException("Inventory not found for this product size"));

        inventory.setStockQuantity(request.getStockQuantity());
        inventory.setLastUpdated(LocalDateTime.now());

        inventoryRepository.save(inventory);

        CreateInventoryResponse response = new CreateInventoryResponse();
        response.setMessage("Successfully updated inventory");
        response.setQuantity(inventory.getStockQuantity());
        response.setProductName(
                inventory.getProductSize().getProduct().getName() + " - " +
                        inventory.getProductSize().getSizeName()
        );

        return response;
    }

    @Transactional
    public InventoryAfterBasketResponse addBasketUpdate(Long productSizeId, int quantity) {

        ProductSize productSize = productSizeRepository.findById(productSizeId)
                .orElseThrow(() -> new ProductException("Product size not found with id: " + productSizeId));

        Inventory inventory = inventoryRepository.findByProductSize(productSize)
                .orElseThrow(() -> new ProductException("Inventory not found for this product size"));

        int availableToReserve = inventory.getStockQuantity() - inventory.getReservedQuantity();
        if (availableToReserve < quantity) {
            throw new ProductException("Not enough stock available to reserve");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventory.setLastUpdated(LocalDateTime.now());

        inventoryRepository.save(inventory);

        InventoryAfterBasketResponse response = new InventoryAfterBasketResponse();
        response.setMessage("Reserved quantity updated successfully");
        response.setProductName(productSize.getProduct().getName() + " - " + productSize.getSizeName());
        response.setRequestedQuantity(quantity);
        response.setReservedQuantity(inventory.getReservedQuantity());
        response.setAvailableStock(inventory.getStockQuantity() - inventory.getReservedQuantity());
        response.setLastUpdated(inventory.getLastUpdated());

        return response;
    }

    @Transactional
    public InventoryAfterBasketResponse removeFromBasketUpdate(Long productSizeId, int quantity) {

        ProductSize productSize = productSizeRepository.findById(productSizeId)
                .orElseThrow(() -> new ProductException("Product size not found with id: " + productSizeId));

        Inventory inventory = inventoryRepository.findByProductSize(productSize)
                .orElseThrow(() -> new ProductException("Inventory not found for this product size"));

        if (inventory.getReservedQuantity() < quantity) {
            throw new ProductException("Cannot remove more than reserved quantity");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);

        if (inventory.getReservedQuantity() < 0) {
            inventory.setReservedQuantity(0);
        }

        inventory.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inventory);

        InventoryAfterBasketResponse response = new InventoryAfterBasketResponse();
        response.setMessage("Reserved quantity decreased successfully");
        response.setProductName(productSize.getProduct().getName() + " - " + productSize.getSizeName());
        response.setRequestedQuantity(quantity);
        response.setReservedQuantity(inventory.getReservedQuantity());
        response.setAvailableStock(inventory.getStockQuantity() - inventory.getReservedQuantity());
        response.setLastUpdated(inventory.getLastUpdated());

        return response;
    }


    @Transactional
    public InventoryAfterBasketResponse confirmOrderStockUpdate(Long productSizeId, int quantity) {

        ProductSize productSize = productSizeRepository.findById(productSizeId)
                .orElseThrow(() -> new ProductException("Product size not found with id: " + productSizeId));

        Inventory inventory = inventoryRepository.findByProductSize(productSize)
                .orElseThrow(() -> new InventoryException("Inventory not found for product size id: " + productSizeId));

        if (quantity <= 0) {
            throw new InventoryException("Quantity must be greater than 0");
        }

        if (inventory.getReservedQuantity() < quantity) {
            throw new InventoryException("Not enough reserved quantity to confirm order");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setStockQuantity(inventory.getStockQuantity() - quantity);
        inventory.setLastUpdated(LocalDateTime.now());

        inventoryRepository.save(inventory);

        InventoryAfterBasketResponse response = new InventoryAfterBasketResponse();
        response.setMessage("Order confirmed and stock updated successfully");
        response.setProductName(inventory.getProductSize().getProduct().getName());
        response.setRequestedQuantity(quantity);
        response.setReservedQuantity(inventory.getReservedQuantity());
        response.setAvailableStock(inventory.getStockQuantity());
        response.setLastUpdated(inventory.getLastUpdated());

        return response;
    }

}

