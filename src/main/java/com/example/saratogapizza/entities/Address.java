package com.example.saratogapizza.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) //just include id
@ToString(exclude = "user") // block the loop
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // just addressId on equals/hashCode
    private Long addressId;

    private String street;
    private String buildingNo;
    private String floorNo;
    private String apartmentNo;
    private String addressName;
    private String addressNote;
    private String district;
    private String city;
    private String country;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
