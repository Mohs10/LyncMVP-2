package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AdminAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminAddressId;

    private String address;
    private String city;
    private String state;
    private String country;
    private Integer pincode;
}
