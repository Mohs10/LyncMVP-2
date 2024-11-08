package com.example.Lync.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String qId;
    private String buyerUId; // Reference to buyer (User)
    private Long productId; // Reference to Product
    private Long osId;

    //Order Specification
    private Double quantity;
    private String quantityUnit;
    private String priceTerms;
    //private Boolean certificate;
    private Double askMinPrice;
    private Double askMaxPrice;
    private String priceUnit;
    private Boolean npop;
    private Boolean nop;
    private Boolean eu;
    private Boolean gsdc;
    private Boolean ipm;
    private Boolean other;
    private String otherCertification;
    private String packagingMaterial;
    private String paymentTerms;
    private String targetLeadTime;
    private String deliveryAddress;
    private String country;
    private String state;
    private String city;
    private int pincode;
    private LocalDate specifyDeliveryDate;

    //Product Specification
    private Double chalkyGrains;
    private String grainSize;
    private Double kettValue;
    private Double moistureContent;
    private String brokenGrain;
    private String admixing;
    private String dd;



    private LocalDate raiseDate = LocalDate.now();
    private LocalTime raiseTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
    private String orderStatus;
    private String sellerUId; // Reference to seller (User)
    private Double sentPrice;
    private LocalDate sentDate;
    private LocalTime sentTime;
    private String unit;
    // Getters and Setters
}

