package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.BusinessDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessDetailsRepository extends JpaRepository<BusinessDetails, Long> {
}
