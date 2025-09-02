package com.example.saratogapizza.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;


    @Column(unique = true, nullable = false)
    private String categoryId;

    @ManyToOne
    private Category parentCategory;

    @Column(nullable = false)
    private Integer level;


}
