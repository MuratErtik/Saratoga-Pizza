package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.*;
import com.example.saratogapizza.exceptions.CategoryException;
import com.example.saratogapizza.exceptions.ProductException;
import com.example.saratogapizza.repositories.CategoryRepository;
import com.example.saratogapizza.repositories.DealItemRepository;
import com.example.saratogapizza.repositories.DealRepository;
import com.example.saratogapizza.repositories.ProductRepository;
import com.example.saratogapizza.requests.CreateDealItemRequest;
import com.example.saratogapizza.requests.CreateDealRequest;
import com.example.saratogapizza.requests.CreateProductRequest;
import com.example.saratogapizza.responses.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private final DealRepository dealRepository;

    private final DealItemRepository dealItemRepository;

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

    public DeleteProductResponse deleteProduct(Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductException("Product not found"));

        productRepository.delete(product);

        DeleteProductResponse response = new DeleteProductResponse();
        response.setMessage("Product deleted successfully");
        return response;
    }

    public UpdateProductResponse updateProduct(CreateProductRequest request, Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductException("Product not found"));

        if(request.getName()!=null) product.setName(request.getName());

        if(request.getDescription()!=null) product.setDescription(request.getDescription());

        if(request.getPrice()!=null){
            if (request.getPrice().intValue()<0){
                throw new ProductException("Price cannot be negative");
            }
            product.setPrice(request.getPrice());
        }

        //imagessss do not forget

        if (request.isVegan()!=product.isVegan()) product.setVegan(request.isVegan());

        if (request.isVegetarian()!=product.isVegetarian()) product.setVegetarian(request.isVegetarian());

        if (request.getSpicyLevel()<0){
                throw new ProductException("Spicy level cannot be negative");
        }
        product.setSpicyLevel(request.getSpicyLevel());

        if(request.getPreparationTime()!=null){
            if (request.getPreparationTime()<0){
                throw new ProductException("Preparation time cannot be negative");
            }
            product.setPreparationTime(request.getPreparationTime());
        }

        if (request.getAllergens()!=null) product.setAllergens(request.getAllergens());

        if (request.getTags()!=null) product.setTags(request.getTags());

        if (request.isCustomizable()!=product.isCustomizable()) product.setCustomizable(request.isCustomizable());


        UpdateProductResponse response = new UpdateProductResponse();
        response.setMessage("Product updated successfully");
        return response;
    }

    public List<GetAllProductResponse> searchProduct(String search) {

        List<Product> products = productRepository.findByNameContainingIgnoreCase(search);

        return products.stream().map(this::mapToGetAllProductResponse).collect(Collectors.toList());

    }

    @Transactional
    public CreateDealResponse createDeal(CreateDealRequest request) throws IOException {



        Deal deal = new Deal();

        deal.setTitle(request.getTitle());
        deal.setDescription(request.getDescription());

        if (request.getDealPrice().intValue()<0){
            throw new ProductException("Price cannot be negative");
        }

        deal.setDealPrice(request.getDealPrice());
        deal.setStartDate(request.getStartDate());
        deal.setEndDate(request.getEndDate());
        deal.setActive(request.isActive());

        if (request.getImages()!=null && !request.getImages().isEmpty()){
            List<String> images = imageUploadService.uploadImages(request.getImages());
            deal.setImages(images);
        }

        Category category = categoryRepository.findByName(request.getCategoryName());
        deal.setCategory(category);

        List<DealItem> dealItems = new ArrayList<>();

        request.getDealItems().stream().map(dealItem -> mapToDealItem(dealItem, deal)).forEach(dealItems::add);

        deal.setItems(dealItems);

        dealItemRepository.saveAll(dealItems);

        dealRepository.save(deal);

        CreateDealResponse response = new CreateDealResponse();
        response.setMessage("Deal created successfully");
        return response;




    }

    private DealItem mapToDealItem(CreateDealItemRequest request, Deal deal) {

        DealItem dealItem = new DealItem();

        Product product = productRepository.findByName(request.getProductName());

        if (product == null) throw new ProductException("Product not found");

        dealItem.setProduct(product);

        dealItem.setQuantity(request.getQuantity());

        dealItem.setDeal(deal);

        return dealItem;
    }


    public List<GetAllDealResponse> getAllDeal() {

        List<Deal> deals = dealRepository.findAll();

        return deals.stream().map(this::mapToGetAllDealResponse).collect(Collectors.toList());



    }

    private GetAllDealResponse mapToGetAllDealResponse(Deal deal){

        GetAllDealResponse response = new GetAllDealResponse();

        response.setId(deal.getId());

        response.setTitle(deal.getTitle());

        response.setDescription(deal.getDescription());

        response.setDealPrice(deal.getDealPrice());

        response.setStartDate(deal.getStartDate());
        response.setEndDate(deal.getEndDate());

        response.setImages(deal.getImages());

        String categoryName = deal.getCategory().getName();

        response.setCategoryName(categoryName);


        response.setItems(deal.getItems().stream().map(this::mapToDealItemResponse).collect(Collectors.toList()));

        return response;


    }

    private DealItemResponse mapToDealItemResponse(DealItem dealItem){
        DealItemResponse response = new DealItemResponse();
        response.setId(dealItem.getId());
        response.setQuantity(dealItem.getQuantity());
        response.setProductName(dealItem.getProduct().getName());
        return response;

    }


    public GetAllDealResponse getDeal(Long dealId) {

        Deal deal = dealRepository.findById(dealId).orElseThrow(() -> new ProductException("Deal not found with id: " + dealId));

        return mapToGetAllDealResponse(deal);
    }

    public List<GetAllDealResponse> searchDeal(String search) {

        List<Deal> deals = dealRepository.findByNameContainingIgnoreCase(search);
        return deals.stream().map(this::mapToGetAllDealResponse).collect(Collectors.toList());
    }


    public DeleteDealResponse deleteDeal(Long dealId) {

        Deal deal = dealRepository.findById(dealId).orElseThrow(() -> new ProductException("Deal not found with id: " + dealId));
        dealRepository.delete(deal);
        DeleteDealResponse response = new DeleteDealResponse();
        response.setMessage("Deal deleted successfully");
        return response;

    }


}
