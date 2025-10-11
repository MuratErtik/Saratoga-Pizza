package com.example.saratogapizza.responses;

import com.example.saratogapizza.entities.Category;
import com.example.saratogapizza.entities.DealItem;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetAllDealResponse {


    private Long id;

    private String title;            // Family menu
    private String description;      // 2 mid pizza + 1 cola
    private BigDecimal dealPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private List<String> images;

    private String categoryName;

    private List<DealItemResponse> items;
}
