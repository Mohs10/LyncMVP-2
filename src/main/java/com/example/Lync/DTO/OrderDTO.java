package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private String oId;
    private String qId;
    private String buyerUId; // Reference to buyer (User)
    private String sellerUId; // Reference to seller (User)
    private Long pId; // Reference to Product
    private Double productQuantity;
    private Double buyerFinalPrice;
    private Double buyerPaid;
    private Double sellerFinalPrice;
    private Long adminAddressId;
    private Long buyerAddressId;

    private String buyerPurchaseOrderURL;
    private String adminPurchaseInvoiceURL;
}
