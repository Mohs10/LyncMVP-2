package com.example.Lync.DTO;

import com.example.Lync.Entity.SellerNegotiate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDTO {

    private String qId; //same orderId- oId
    private String buyerUId; // Reference to buyer (User)

    // Product details
    private Long productId; // Reference to Product
    private Long productFormId; // ID for the form of the product
    private Long productVarietyId;

    private String productName;
    private String FormName;
    private String varietyName;
    // Product image
    private String productImageUrl;


    //Order Specification
    private Double quantity;
    private String quantityUnit;
    private String priceTerms;
//    private Boolean certificate;
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
    private String sellerUId; // Reference to seller (User)
    private Double sellerFinalPrice;
    private Double buyerFinalPrice;
    private LocalDate sentDate;
    private LocalTime sentTime;
    private String unit;

    private Long osId;
//    private Long oId; // Reference to orderId
//    private String status;
    private LocalDate date;
    private LocalTime time;
    private String description;
    private String imageUrl;
    private String location;

    private List<String> sellerUIds; //Inquiry can be sent to multiple seller, so this is to capture sellers list.
    private Long adminAddressId;
    private List<SellerNegotiateDTO> sellerNegotiations;
    private String avgLeadTime;

    //Buyer Negotiate
    private Double adminInitialPrice;
    private String comment;
    private String paymentTerm;
    private LocalDate aipDate;
    private LocalTime aipTime;
    private Double buyerNegotiatePrice;
    private LocalDate bnpDate;
    private LocalTime bnpTime;
    private Double adminFinalPrice;
    private LocalDate afpDate;
    private LocalTime afpTime;
    private String status;

    //Invoice Url
    private String invoiceUrl;

    //List of dynamic Specifications
    private List<SpecificationDTO> specifications;

}
