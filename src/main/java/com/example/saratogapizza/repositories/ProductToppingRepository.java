package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.Product;
import com.example.saratogapizza.entities.ProductSize;
import com.example.saratogapizza.entities.ProductTopping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductToppingRepository  extends JpaRepository<ProductTopping, Long> {

    Optional<ProductTopping> findByToppingNameAndProduct(String name, Product product);
}
