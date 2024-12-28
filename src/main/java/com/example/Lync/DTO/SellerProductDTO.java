package com.example.Lync.DTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Getter
@Setter

public class SellerProductDTO {
    private String spId;
    private String sellerId;
    private Long productId;

    // Product details
    private Long productFormId;
    private Long productVarietyId;

    private String productName;

    // Product details
    private String productFormName;
    private String productVarietyName;

    private String originOfProduce;
    private Double availableAmount;
    private String unit;
    private String description;

//    private MultipartFile productImage1;
//    private MultipartFile productImage2;
    private String productImageUrl1;
    private String productImageUrl2;

    // Pricing details
    private Double maxPrice;
    private Double minPrice;
    private Double deliveryCharges;
    private String priceTerms;

    // Packaging & Payment
    private String packagingMaterial;
    private String paymentTerms;

    // Availability
    private LocalDate earliestAvailableDate;

    // Warehouse location
    private String warehouseCountry;
    private String warehouseState;
    private String warehouseCity;
    private String warehousePinCode;


    private String certificationName;
    private String certificationFileUrl;
//    private MultipartFile certificationFile;

    private List<SpecificationDTO> specifications;

    private String message; //for criteria matching propose in Inquiry part
    private int priority;

}

