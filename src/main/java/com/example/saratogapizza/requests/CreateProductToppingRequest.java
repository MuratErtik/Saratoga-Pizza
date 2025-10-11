package com.example.saratogapizza.requests;

import com.example.saratogapizza.entities.Product;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class CreateProductToppingRequest {

    private String toppingName; // Example: "Extra Range source"

    private BigDecimal extraPrice;

    private MultipartFile imageUrl;
}
