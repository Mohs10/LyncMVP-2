package com.example.Lync.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerProduct {

    @Id
    private String spId; // Unique ID for seller's product
    private String sellerId; // Unique ID for seller
    private Long productId;

    // Product details
    private Long productFormId; // ID for the form of the product
    private Long productVarietyId;
    private String originOfProduce;
    private Double availableAmount;
    private String unit;
    private String description;

    // Pricing details
    private Double maxPrice;
    private Double minPrice;
    private Double deliveryCharges;
    private String priceTerms;

    // Image URLs
    private String productImageUrl1;
    private String productImageUrl2;

    // Packaging & Payment
    private String packagingMaterial;
    private String paymentTerms;

    // Availability
    private LocalDate earliestAvailableDate;

    private String warehouseCountry;
    private String warehouseState;
    private String warehouseCity;
    private String warehousePinCode;


    private String certificationName;
    private String certificationFileUrl;

    // Relationship with SellerProductSpecification
    @ManyToMany
    @JoinTable(
            name = "seller_product_specifications",
            joinColumns = @JoinColumn(name = "sp_id"),
            inverseJoinColumns = @JoinColumn(name = "seller_specification_id")
    )
    private List<SellerProductSpecification> specifications;

}




