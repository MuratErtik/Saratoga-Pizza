package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.Product;
import com.example.saratogapizza.entities.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {

    Optional<ProductSize> findBySizeNameAndProduct(String name, Product product);
}
