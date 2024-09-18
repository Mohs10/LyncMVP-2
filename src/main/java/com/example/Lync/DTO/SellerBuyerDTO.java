package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerBuyerDTO {
    private String userId;
    private String fullName;
    private String email;
    private String password;


    private String phoneNumber;
    private String country;
    private String state;
    private String city;
    private String pinCode;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean seller;
    private Boolean buyer;


    private LocalDate incorporationDate;
    private String companyName;
    private String gstIn;
    private String companyLocation;
    private String wareHouseAddress;
    private Boolean storageLicense;

    private String profilePictureUrl;
    private String storageLicenseFileUrl;

    // Using MultipartFile for image and file uploads
    private MultipartFile profilePicture;
    private MultipartFile storageLicenseFile;
}

