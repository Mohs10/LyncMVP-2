package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RazorpayOrderDTO {

    private String id;
    private String receipt;
    private String status;
    private int amount;
    private String currency;
    private int amountPaid;       // Added field for amount paid
    private int amountDue;        // Added field for amount due
    private long createdAt;       // Added field for creation timestamp
    private int attempts;         // Added field for the number of attempts
}
