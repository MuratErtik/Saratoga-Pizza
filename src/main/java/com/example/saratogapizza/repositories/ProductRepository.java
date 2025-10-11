package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Product findByName(String name);

    Product findById(long productId);

    List<Product> findByNameContainingIgnoreCase(String name);
}
