package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SellerNegotiate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long snId;
    private String qId;
    private String sellerUId; // Reference to seller (User)
    private String spId;
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
