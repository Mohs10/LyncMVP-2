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

    private String sellerUId;
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
