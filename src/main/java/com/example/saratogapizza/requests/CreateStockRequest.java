package com.example.saratogapizza.requests;

import com.example.saratogapizza.entities.ProductSize;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateStockRequest {


    private int stockQuantity;

}
