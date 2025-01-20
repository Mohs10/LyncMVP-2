package com.example.Lync.DTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayPaymentDTO {

    private String id;
    private String orderId;
    private String contact;
    private String email;
    private String currency;
    private int amount;
    private int amountRefunded;
    private String method;
    private String status;
    private long createdAt;
    private int fee;
    private String description;
    private String errorReason;
    private String errorDescription;
    private String errorSource;
    private String errorStep;
    private String errorCode;
    private boolean international;
    private String wallet;
    private String vpa;
    private String invoiceId;
    private String notes;
    private String acquirerAuthCode;
    private String tax;
    private RazorpayCardDTO cardDTO;
}

