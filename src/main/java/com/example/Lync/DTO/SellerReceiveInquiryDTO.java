package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerReceiveInquiryDTO {
    private String qId;
    private Long productId; // Reference to Product
    private String productName;
    private String FormName;
    private String varietyName;
    private String sellerUId; // Reference to seller (User)
    private String sellerName;
    private String type;
    private Long productFormId; // ID for the form of the product
    private Long productVarietyId;
    private String productImageURL;

    private Boolean optedSample;
    private Boolean optedTesting;
    private Boolean optedOrder;

    //Order Specification
    private Double quantity;
    private String quantityUnit;


    //Certificate;
    private Boolean npop;
    private Boolean nop;
    private Boolean eu;
    private Boolean gsdc;
    private Boolean ipm;
    private Boolean other;
    private String otherCertification;

    private String packagingMaterial;

    //Product Specification
    private Double chalkyGrains;
    private String grainSize;
    private Double kettValue;
    private Double moistureContent;
    private String brokenGrain;
    private String admixing;
    private String dd;

    //SellerNegotiate
    private Long snId;
    private Double adminInitialPrice;
    private LocalDate aipDate;
    private LocalTime aipTime;
    private String avgLeadTime;
    private Long adminAddressId;
    private Double sellerNegotiatePrice;
    private LocalDate snpDate;
    private LocalTime snpTime;
    private Double adminFinalPrice;
    private LocalDate afpDate;
    private LocalTime afpTime;
    private String status;

    //Transaction Certificate
    private boolean buyerWantsTC;

    //List of dynamic Specifications
    private List<SpecificationDTO> specifications;
}