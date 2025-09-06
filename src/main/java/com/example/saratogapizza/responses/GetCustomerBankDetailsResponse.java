package com.example.saratogapizza.responses;

import lombok.Data;

@Data
public class GetCustomerBankDetailsResponse {

    private String accountHolderName;

    private String bankName;

    private String accountNumber;

    private String accountName;

    private String lastValidDate;

    private String cvv;

}
