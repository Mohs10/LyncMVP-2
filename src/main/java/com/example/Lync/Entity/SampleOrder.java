package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SampleOrder {

    @Id
    private String soId;
    private String qId;
    private String buyerUId;
    private String sellerUId;

    private Long productId;
    private Long productFormId;
    private Long productVarietyId;

    private Double buyerQuantity;
    private String buyerUnit;
    private Long buyerAddressId;
    private Double buyerAmount;
    private LocalDate buyerRequestDate;
    private LocalTime buyerRequestTime;

    private String adminSendQtyToSeller;
    private String adminUnit;
    private Long adminAddressId;
    private LocalDate adminEDDToSeller;
    private LocalDate adminSendToSellerDate;
    private LocalTime adminSendToSellerTime;
//
//    private LocalDate sellerRespondDate;
//    private LocalTime sellerRespondTime;

    private LocalDate sellerPackagingDate;
    private LocalTime sellerPackagingTime;

    private LocalDate sellerDispatchDate;
    private LocalTime sellerDispatchTime;

    private LocalDate adminReceiveDate;
    private LocalTime adminReceiveTime;

    private LocalDate adminProcessingDate;
    private LocalTime adminProcessingTime;

    private LocalDate adminDispatchDate;
    private LocalTime adminDispatchTime;

    private LocalDate buyerReceiveDate;
    private LocalTime buyerReceiveTime;

    private LocalDate buyerApproveDate;
    private LocalTime buyerApproveTime;

    private LocalDate buyerRejectDate;
    private LocalTime buyerRejectTime;
    private String currentStatus;
}
