package com.example.Lync.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String qId;
    private String buyerId; // Reference to buyer (User)
    private Long osId;

    // Product details
    private Long productId; // Reference to Product
    private Long productFormId; // ID for the form of the product
    private Long productVarietyId;

    //Order Specification
    private Double quantity;
    private String quantityUnit;
    private String priceTerms;
    //private Boolean certificate;
    private Double askMinPrice; //buyer price
    private Double askMaxPrice; //buyer price
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
//    private Double chalkyGrains;
//    private String grainSize;
//    private Double kettValue;
//    private Double moistureContent;
//    private String brokenGrain;
//    private String admixing;
//    private String dd;


    private LocalDate raiseDate = LocalDate.now();
    private LocalTime raiseTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);

    private String orderStatus;
    private String sellerUId; // Reference to seller(User) selectedSeller
    private Double sellerFinalPrice; //final price
    private Double buyerFinalPrice;
    private LocalDate sentDate;
    private LocalTime sentTime;
    private String unit;
    // Getters and Setters

    //Invoice Url
    private String invoiceUrl;

    @ManyToMany
    @JoinTable(
            name = "inquiry_specifications",
            joinColumns = @JoinColumn(name = "q_id"),
            inverseJoinColumns = @JoinColumn(name = "inquiry_specification_id")
    )
    private List<InquirySpecification> specifications;

    private String purchaseOrderUrl; // Admin will upload the purchase order
}

