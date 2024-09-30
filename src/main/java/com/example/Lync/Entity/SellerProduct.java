package com.example.Lync.Entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
public class SellerProduct {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String spId; // Unique ID for seller's product

    private String sellerId; // Unique ID for seller's product

    private Double maxPricePerTon;
    private Double deliveryCharges;
    private String description;
    private String pVerity;
    private String grainSize;
    private String admixing;
    private Double moisture;
    private Double dd;
    private Double kettValue;
    private Double chalky;
    private Double foreignMaterial;
    private String warehouse;
    private Double availableAmount;
    private String pImageUrl1;
    private String pImageUrl2;
    private String pCertificationUrl;
    private LocalDate addDate;
    private LocalTime addTime;
    private LocalDate earliestAvailableDate;
    private Long pId; // Reference to Product without join

    // Getters and Setters
}

