package com.example.saratogapizza.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;            // Family menu
    private String description;      // 2 mid pizza + 1 cola
    private BigDecimal dealPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;

    @ElementCollection
    private List<String> images;

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DealItem> items;
}