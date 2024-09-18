package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
@Entity
public class SellerBuyerrLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ulId;
    private String uId; // Reference to userId
    private String userProfile; // Indicates seller/buyer
    private String location;

    // Getters and Setters
}

