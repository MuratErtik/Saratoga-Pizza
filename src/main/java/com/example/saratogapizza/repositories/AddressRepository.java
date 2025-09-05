package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.Address;
import com.example.saratogapizza.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByUserAndAddressName(User user, String addressName);
}
