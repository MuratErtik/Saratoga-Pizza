package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.Inventory;
import com.example.saratogapizza.entities.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findById(Long id);

    Optional<Inventory> findByProductSize(ProductSize productSize);
}
