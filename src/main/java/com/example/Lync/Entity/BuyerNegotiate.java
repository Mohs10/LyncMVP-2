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
public class BuyerNegotiate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bnId;
    private String qId;
    private String buyerUId; // Reference to buyer (User)
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
}
