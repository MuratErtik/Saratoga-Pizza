package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.Category;
import com.example.saratogapizza.entities.Product;
import com.example.saratogapizza.entities.ProductSize;
import com.example.saratogapizza.entities.ProductTopping;
import com.example.saratogapizza.exceptions.CategoryException;
import com.example.saratogapizza.exceptions.ProductException;
import com.example.saratogapizza.repositories.CategoryRepository;
import com.example.saratogapizza.repositories.ProductRepository;
import com.example.saratogapizza.requests.CreateProductRequest;
import com.example.saratogapizza.responses.CreateProductResponse;
import com.example.saratogapizza.responses.GetAllProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ImageUploadService imageUploadService;

    private final CategoryRepository categoryRepository;

    public CreateProductResponse createProduct(CreateProductRequest request) throws IOException {

        //check first product have been created already or not
        if (productRepository.findByName(request.getName())!=null){
            throw new ProductException("Product already exists with name: " + request.getName());
        }

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());

        if (request.getPrice().intValue()<0){
            throw new ProductException("Price cannot be negative");
        }
        product.setPrice(request.getPrice());

        if (request.getImages()!=null && !request.getImages().isEmpty()){
            List<String> images = imageUploadService.uploadImages(request.getImages());
            product.setImages(images);
        }

        product.setVegan(request.isVegan());

        product.setVegetarian(request.isVegetarian());

        product.setSpicyLevel(request.getSpicyLevel());

        product.setPreparationTime(request.getPreparationTime());

        product.setAllergens(request.getAllergens());


        //handle category
        Category category = categoryRepository.findByCategoryId(request.getCategoryId());
        if (category==null){
            throw new CategoryException("Category not found");
        }
        product.setCategory(category);

        product.setTags(request.getTags());

        product.setCustomizable(request.isCustomizable());

        productRepository.save(product);

        CreateProductResponse response = new CreateProductResponse();
        response.setMessage("Product created successfully");
        response.setName(product.getName());
        log.info("Product created successfully");
        return response;


    }

    public List<GetAllProductResponse> getAllProducts() {

        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToGetAllProductResponse).collect(Collectors.toList());

    }

    private GetAllProductResponse mapToGetAllProductResponse(Product product) {
        GetAllProductResponse response = new GetAllProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setImages(product.getImages());
        response.setAvailable(product.isAvailable());
        response.setVegan(product.isVegan());
        response.setVegetarian(product.isVegetarian());
        response.setSpicyLevel(product.getSpicyLevel());
        response.setPreparationTime(product.getPreparationTime());
        response.setDiscount(product.getDiscount());
        response.setRating(product.getRating());
        response.setAllergens(product.getAllergens());
        response.setTags(product.getTags());
        response.setCustomizable(product.isCustomizable());
        String categoryName = product.getCategory().getName();
        response.setCategoryName(categoryName);
        response.setSizes(product.getSizes());
        response.setToppings(product.getToppings());
        return response;

    }

    public List<GetAllProductResponse> getProductsByFilters(
            Long categoryId,
            BigDecimal price,
            boolean isVegetarian,
            boolean isVegan,
            int spicyLevel,
            Double rating,
            String tags) {

        Specification<Product> spec = (root, query, cb) -> cb.conjunction();

        if (categoryId != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));

        if (price != null)
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), price));

        if (isVegetarian)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isVegetarian"), true));

        if (isVegan)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isVegan"), true));

        if (spicyLevel > 0)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("spicyLevel"), spicyLevel));

        if (rating != null)
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("rating"), rating));

        if (tags != null && !tags.isBlank())
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("tags")), "%" + tags.toLowerCase() + "%"));

        return productRepository.findAll(spec)
                .stream()
                .map(this::mapToGetAllProductResponse)
                .toList();
    }

    public GetAllProductResponse getProduct(Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductException("Product not found"));

        return mapToGetAllProductResponse(product);
    }
}
