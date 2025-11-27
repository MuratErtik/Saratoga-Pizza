package com.example.saratogapizza.repositories;


import com.example.saratogapizza.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
