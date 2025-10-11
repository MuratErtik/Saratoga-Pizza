package com.example.saratogapizza.responses;

import lombok.Data;

@Data
public class BusinessAddressResponse {
    private String street;        // Cadde/Sokak
    private String buildingNo;    // Bina No
    private String addressName;   // Adres Adı (ör. Ofis, Ev)

    private String district;      // İlçe (ör: Sancaktepe)
    private String city;          // Şehir (ör: Istanbul)
    private String country;       // Ülke (ör: Turkey)

}
