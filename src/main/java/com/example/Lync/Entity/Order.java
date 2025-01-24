package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String oId;
    private String qId;
    private String buyerUId; // Reference to buyer (User)
    private String sellerUId; // Reference to seller (User)
    private Long pId; // Reference to Product
    private Double productQuantity;
    private Double buyerFinalPrice;
    private Double buyerPaid;
    private Double sellerFinalPrice;
    private Long adminAddressId;
    private Long buyerAddressId;
    private String paymentId;

    private String buyerPurchaseOrderURL;
    private LocalDate buyerPurchaseOrderURLDate;
    private LocalTime buyerPurchaseOrderURLTime;

    private String adminPurchaseInvoiceURL;
    private LocalDate adminPurchaseInvoiceURLDate;
    private LocalTime adminPurchaseInvoiceURLTime;

    private String adminPurchaseOrderURL;
    private LocalDate adminPurchaseOrderURLDate;
    private LocalTime adminPurchaseOrderURLTime;

    private Double adminNotifyBuyerToPay;
    private LocalDate adminNotifyBuyerToPayDate;
    private LocalTime adminNotifyBuyerToPayTime;

    private Boolean buyer1stPayment;
    private LocalDate buyer1stPaymentDate;
    private LocalTime buyer1stPaymentTime;

    private Boolean buyerClearedPayment;
//
//    private Boolean adminConfirmBuyerPayment;
//    private LocalDate adminConfirmBuyerPaymentDate;
//    private LocalTime adminConfirmBuyerPaymentTime;

    private Boolean adminNotifySellerToDispatch;
    private LocalDate adminNotifySellerToDispatchDate;
    private LocalTime adminNotifySellerToDispatchTime;

    private LocalDate sellerProcessingOrderDate;
    private LocalTime sellerProcessingOrderTime;

    private LocalDate sellerDispatchOrderDate;
    private LocalTime sellerDispatchOrderTime;

    private LocalDate sellerDispatchPeriod;

    private String sellerCourierCompany;
    private String sellerOrderTrackerId;

    private String sellerOrderLoadingVehicleImg;
    private LocalDate sellerOrderLoadingVehicleImgDate;
    private LocalTime sellerOrderLoadingVehicleImgTime;

    private String sellerLoadedSealedVehicleImg;
    private LocalDate sellerLoadedSealedVehicleImgDate;
    private LocalTime sellerLoadedSealedVehicleImgTime;

    private String sellerEWayBill;
    private LocalDate sellerEWayBillDate;
    private LocalTime sellerEWayBillTime;

    private String sellerPaymentInvoice;
    private LocalDate sellerPaymentInvoiceDate;
    private LocalTime sellerPaymentInvoiceTime;

    private String sellerLRCopy;
    private LocalDate sellerLRCopyDate;
    private LocalTime sellerLRCopyTime;

    private String sellerWeightSlipPreLoad;
    private LocalDate sellerWeightSlipPreLoadDate;
    private LocalTime sellerWeightSlipPreLoadTime;

    private String sellerWeightSlipPostLoad;
    private LocalDate sellerWeightSlipPostLoadDate;
    private LocalTime sellerWeightSlipPostLoadTime;

    private LocalDate adminReceivedOrderDate;
    private LocalTime adminReceivedOrderTime;

    private LocalDate adminProcessingOrderDate;
    private LocalTime adminProcessingOrderTime;

    private LocalDate adminDispatchedOrderDate;
    private LocalTime adminDispatchedOrderTime;

    private String adminCourierCompany;
    private String adminOrderTrackerId;

    private String adminEWayBill;
    private LocalDate adminEWayBillDate;
    private LocalTime adminEWayBillTime;

    private String adminPaymentInvoice;
    private LocalDate adminPaymentInvoiceDate;
    private LocalTime adminPaymentInvoiceTime;

    private String adminLRCopy;
    private LocalDate adminLRCopyDate;
    private LocalTime adminLRCopyTime;

    private String adminWeightSlipPreLoad;
    private LocalDate adminWeightSlipPreLoadDate;
    private LocalTime adminWeightSlipPreLoadTime;

    private String adminWeightSlipPostLoad;
    private LocalDate adminWeightSlipPostLoadDate;
    private LocalTime adminWeightSlipPostLoadTime;

    private String sellerTransactionCertificate;
    private LocalDate sellerTransactionCertificateDate;
    private LocalTime sellerTransactionCertificateTime;

    private String adminTransactionCertificate;
    private LocalDate adminTransactionCertificateDate;
    private LocalTime adminTransactionCertificateTime;

    private LocalDate buyerReceivedOrderDate;
    private LocalTime buyerReceivedOrderTime;




    // Getters and Setters
}
