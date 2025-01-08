package com.example.Lync.DTO;

import com.example.Lync.Entity.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderInTestingDTO {

    private String testId; // Format: "TEST-YYYY-XXXXX"
    private String buyerId; // Buyer ID
    private String sellerId; // Seller ID
    private String queryId; // Query ID

    private String productName;
    private String productImageUrl;
    private String variety;
    private Double quantity;
    private Double price;
    private String location;




}