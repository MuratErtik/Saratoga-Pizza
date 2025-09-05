package com.example.saratogapizza.requests;

import lombok.Data;

@Data
public class AddressRequest {
    private String street;        // Cadde/Sokak
    private String buildingNo;    // Bina No
    private String floorNo;       // Kat No
    private String apartmentNo;   // Daire No
    private String addressName;   // Adres Adı (ör. Ofis, Ev)
    private String addressNote;   // Adres Tarifi (isteğe bağlı)

    private String district;      // İlçe (ör: Sancaktepe)
    private String city;          // Şehir (ör: Istanbul)
    private String country;       // Ülke (ör: Turkey)
}
