package com.example.Lync.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayCardDTO {

    private String id;
    private String network;
    private String type;
    private String subType;
    private boolean emi;
    private String issuer;
    private String last4;
    private boolean international;
}

