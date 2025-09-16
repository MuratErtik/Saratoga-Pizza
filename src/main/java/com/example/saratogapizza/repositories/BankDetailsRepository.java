package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.Address;
import com.example.saratogapizza.entities.BankDetails;
import com.example.saratogapizza.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {

    Set<BankDetails> findByUser(User user);

    BankDetails findByUserAndAccountName(User user, String accountName);

    BankDetails findByBankDetailsId(Long bankDetailsId);
}
