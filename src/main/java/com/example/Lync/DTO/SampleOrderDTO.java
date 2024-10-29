package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleOrderDTO {
    //SampleOrder Table
    private String soId;
//    private String qId;
//    private String buyerUId;
//    private String sellerUId;
//    private Long productId;
    private Double sOQuantity;
    private String stockLocation;
    private String deliveryLocation;

    //OrderStatus Table
    private String status;
    private String description;
    private String imageUrl;
    private String location;
}
