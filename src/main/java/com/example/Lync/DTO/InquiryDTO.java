package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDTO {

    private String qId; //same orderId- oId
    private String buyerUId; // Reference to buyer (User)
    private Long productId; // Reference to Product
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

    private Long osId;
//    private Long oId; // Reference to orderId
//    private String status;
    private LocalDate date;
    private LocalTime time;
    private String description;
    private String imageUrl;
    private String location;

}
