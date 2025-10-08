package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByName(String name);
}
