package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
@Entity
public class SellerBuyerAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uaId;

    private String uId; // Reference to userId
    private String userProfile; // Indicates seller/buyer

    private String address;
    private String city;
    private String state;
    private String country;
    private Integer pincode;

    // Getters and Setters
}

