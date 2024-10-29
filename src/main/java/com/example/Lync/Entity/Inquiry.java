package com.example.Lync.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String qId;
    private String buyerUId; // Reference to buyer (User)
    private Long productId; // Reference to Product
    private Long osId;
    private Double quantity;
    private Boolean certificate;
    private Double askPrice;
    private String shipAddress;
    private LocalDate raiseDate = LocalDate.now();
    private LocalTime raiseTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
    private String orderStatus;
    private String sellerUId; // Reference to seller (User)
    private Double sentPrice;
    private LocalDate sentDate;
    private LocalTime sentTime;

    // Getters and Setters
}

