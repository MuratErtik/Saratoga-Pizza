package com.example.saratogapizza.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    private String street;        // Cadde/Sokak
    private String buildingNo;    // Bina No
    private String floorNo;       // Kat No
    private String apartmentNo;   // Daire No
    private String addressName;   // Adres Adı (ör. Ofis, Ev)
    private String addressNote;   // Adres Tarifi (isteğe bağlı)

    private String district;      // İlçe (ör: Sancaktepe)
    private String city;          // Şehir (ör: Istanbul)
    private String country;       // Ülke (ör: Turkey)


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
