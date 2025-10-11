package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.Address;
import com.example.saratogapizza.entities.Category;
import com.example.saratogapizza.exceptions.CategoryException;
import com.example.saratogapizza.repositories.CategoryRepository;
import com.example.saratogapizza.requests.AddCategoryRequest;
import com.example.saratogapizza.requests.ParentCategoryRequest;
import com.example.saratogapizza.responses.CategoryResponse;
import com.example.saratogapizza.responses.ReturnCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public CategoryResponse createCategory(AddCategoryRequest request, Long userId) {
        // Prevent duplicate
        if (categoryRepository.findByCategoryId(request.getCategoryId()) != null) {
            throw new CategoryException("Category already exists");
        }

        Category category = new Category();
        category.setCategoryId(request.getCategoryId());
        category.setName(request.getName());
        category.setLevel(request.getLevel());

        // Handle parent only if provided
        if (request.getParentCategory() != null) {
            String parentId = request.getParentCategory().getCategoryId();
            Category parent = categoryRepository.findByCategoryId(parentId);

            if (parent == null) {
                throw new CategoryException("Parent category not found: " + parentId);
            }
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null); // top-level category
        }

        categoryRepository.save(category);

        CategoryResponse response = new CategoryResponse();
        response.setMessage("Category created successfully");
        return response;
    }


    private Category mapToCategory(AddCategoryRequest request, Long userId) {
        Category category = new Category();
        category.setName(request.getName());
        category.setCategoryId(request.getCategoryId());
        category.setLevel(request.getLevel());


        if (request.getParentCategory() != null) {
            category.getParentCategory().setCategoryId(request.getParentCategory().getCategoryId());

        } else {
            category.getParentCategory().setCategoryId(null);

        }
        return category;
    }


    public List<ReturnCategoryResponse> getAllCategory() {

        List<Category> categoryList = categoryRepository.findAll();

        return categoryList.stream().map(this::mapToReturnCategoryResponse).collect(Collectors.toList());

    }

    private ReturnCategoryResponse mapToReturnCategoryResponse(Category category) {
        if (category == null) return null;

        ReturnCategoryResponse response = new ReturnCategoryResponse();
        response.setName(category.getName());
        response.setCategoryId(category.getCategoryId());
        response.setLevel(category.getLevel());

        if (category.getParentCategory() != null) {
            response.setCategoryId(category.getParentCategory().getCategoryId());
            response.setName(category.getParentCategory().getName());
        }

        return response;
    }

    public List<CategoryResponse> createCategories(List<AddCategoryRequest> requests, Long userId) {
        return requests.stream()
                .map(req -> createCategory(req,userId))
                .collect(Collectors.toList());
    }

    public CategoryResponse deleteCategory(String categoryId) {
        // Find category
        Category category = categoryRepository.findByCategoryId(categoryId);
        if (category == null) {
            throw new CategoryException("Category not found with id: " + categoryId);
        }

        // Check if it has children
        List<Category> children = categoryRepository.findByParentCategory(category);
        if (children != null && !children.isEmpty()) {
            throw new CategoryException(
                    "Cannot delete category '" + category.getName() + "' because it has subcategories."
            );
        }

        // Safe to delete
        categoryRepository.delete(category);

        CategoryResponse response = new CategoryResponse();
        response.setMessage("Category '" + category.getName() + "' deleted successfully");
        return response;
    }

}
