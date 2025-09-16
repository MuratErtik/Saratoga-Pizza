package com.example.saratogapizza.requests;

import com.example.saratogapizza.entities.BusinessDetails;
import com.example.saratogapizza.entities.User;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
public class BankDetailsRequest {

    private String accountHolderName;

    private String bankName;

    private String iban;

    private String accountNumber;

    private String accountName;

    private String lastValidDate;

    private String cvv;

}
