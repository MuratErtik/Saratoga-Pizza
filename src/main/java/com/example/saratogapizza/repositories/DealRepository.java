package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealRepository extends JpaRepository<Deal, Long> {

    Deal findByTitle(String title);

    Deal findById(long id);

    List<Deal> findByTitleContainingIgnoreCase(String name);
}
