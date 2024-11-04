package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
public class SellerBuyer {
    @Id
    private String userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;
    private String profilePictureUrl;
    private String country;
    private String state;
    private String city;
    private String pinCode;
    private String address;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private Boolean seller;
    private Boolean buyer;
    private Boolean activeUser=true;


    private LocalDate incorporationDate;
    private String companyName;
    private String registrationNumber;
    private String companyEmail;
    private String gstIn;
    private String companyLocation;
    private String wareHouseAddress;
//    private Boolean storageLicense;
//    private String storageLicenseUrl;

    // Additional fields
    private String companyCountry;
    private String companyState;
    private String companyCity;
    private String companyPinCode;

    private String warehouseCountry;
    private String warehouseState;
    private String warehouseCity;
    private String warehousePinCode;

    private Boolean waiveSampleFree = false;



    // Getters and Setters
}

