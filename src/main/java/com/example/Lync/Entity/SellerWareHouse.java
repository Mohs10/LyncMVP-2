package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
public class SellerWareHouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slId;
    private String sellerId; // Reference to userId
    private String wareHouseLocation;
    private String storageLicenseUrl;
    private Boolean storageLicenseAvailable;
    private Boolean thirdPartyStorage;
}

