package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerBuyerAddressDTO {
    private Long uaId;

    private String uId; // Reference to userId
    private String userProfile; // Indicates seller/buyer

    private String address;
    private String city;
    private String state;
    private String country;
    private Integer pincode;
}
