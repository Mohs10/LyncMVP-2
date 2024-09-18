package com.example.Lync.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Query {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qId;
    private String buyerUId; // Reference to buyer (User)
    private Long pId; // Reference to Product
    private Double quantity;
    private Boolean certificate;
    private Double askPrice;
    private String shipAddress;
    private LocalDate raiseDate;
    private LocalTime raiseTime;
    private String orderStatus;
    private String sellerUId; // Reference to seller (User)
    private Double sentPrice;
    private LocalDate sentDate;
    private LocalTime sentTime;

    // Getters and Setters
}

