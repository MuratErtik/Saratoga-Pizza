package com.example.saratogapizza.mappers;

import com.example.saratogapizza.entities.BankDetails;
import com.example.saratogapizza.responses.GetCustomerBankDetailsResponse;

public class BankDetailsMapper {

    public static GetCustomerBankDetailsResponse mapToCustomerBankDetailsResponse(BankDetails bankDetails) {
        GetCustomerBankDetailsResponse response = new GetCustomerBankDetailsResponse();
        response.setAccountHolderName(bankDetails.getAccountHolderName());
        response.setAccountNumber(bankDetails.getAccountNumber());
        response.setBankName(bankDetails.getBankName());
        response.setAccountName(bankDetails.getAccountName());
        response.setLastValidDate(bankDetails.getLastValidDate());
        response.setCvv(bankDetails.getCvv());
        return response;
    }
}
