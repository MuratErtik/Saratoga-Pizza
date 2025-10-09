package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealRepository extends JpaRepository<Deal, Long> {

    Deal findByTitle(String title);
}
