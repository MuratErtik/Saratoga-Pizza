package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.DealItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealItemRepository extends JpaRepository<DealItem, Long> {
}
