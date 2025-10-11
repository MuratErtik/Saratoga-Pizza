package com.example.saratogapizza.requests;

import com.example.saratogapizza.entities.Category;
import com.example.saratogapizza.entities.DealItem;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateDealRequest {

    private String title;            // Family menu
    private String description;      // 2 mid pizza + 1 cola
    private BigDecimal dealPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
    private List<MultipartFile> images;
    private String categoryName = "Deals";
    private List<CreateDealItemRequest> dealItems;

}