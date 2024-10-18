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
    private String productVariety;
    private String grainSize;
    private String admixing;
    private Double moisture;
    private Double dd;
    private Double kettValue;
    private Double chalky;
    private Double foreignMaterial;
    private String warehouse;
    private Double availableAmount;
    private String productImageUrl1;
    private String productImageUrl2;
    private String origin;



    private String productCertifications;
    private String productCertificationUrl;
    private LocalDate addDate;
    private LocalTime addTime;
    private LocalDate earliestAvailableDate;
    private Long productId; // Reference to Product without join


    private Boolean npop;
    private String npopCertification;

    private Boolean nop;
    private String nopCertification;

    private Boolean eu;
    private String euCertification;

    private Boolean gsdc;
    private String gsdcCertification;

    private Boolean ipm;
    private String ipmCertification;


    // Getters and Setters
}

