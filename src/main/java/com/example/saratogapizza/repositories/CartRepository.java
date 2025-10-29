package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.Cart;
import com.example.saratogapizza.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserAndCheckedOutFalse(User user);



}
