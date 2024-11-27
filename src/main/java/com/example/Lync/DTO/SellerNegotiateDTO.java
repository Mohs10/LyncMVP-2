package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerNegotiateDTO {

    private Long snId;
    private String sellerUId;
    private String sellerName;
    private String email;
    private String phoneNumber;
    private String adminCountry;
    private String adminState;
    private String adminCity;
    private String adminPinCode;
    private String adminAddress;

    //seller Product
    private Double availableAmount;
    private Double maxPrice;
    private Double minPrice;

    private Double adminInitialPrice;
    private LocalDate aipDate;
    private LocalTime aipTime;
    private String avgLeadTime;
    private String adminDeliveryAddress;
    private String instruction;
    private Double sellerNegotiatePrice;
    private LocalDate snpDate;
    private LocalTime snpTime;
    private Double adminFinalPrice;
    private LocalDate afpDate;
    private LocalTime afpTime;
    private String status;
    private Long adminAddressId;
}
