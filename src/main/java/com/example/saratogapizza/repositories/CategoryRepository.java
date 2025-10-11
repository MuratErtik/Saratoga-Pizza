package com.example.saratogapizza.repositories;

import com.example.saratogapizza.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByCategoryId(String categoryId);

    List<Category> findByParentCategory(Category parent);

    Category findByName(String name);
}
