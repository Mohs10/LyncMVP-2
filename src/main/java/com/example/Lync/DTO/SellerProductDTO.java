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
}
