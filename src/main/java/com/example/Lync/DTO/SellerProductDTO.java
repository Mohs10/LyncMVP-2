package com.example.Lync.DTO;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Getter
@Setter
public class SellerProductDTO {
    private String spId; // Unique ID for seller's product
    private String sellerId; // Unique ID for seller
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
}