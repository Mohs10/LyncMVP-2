package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestPhase5DTO {

    private String paymentStatus; // Status: PENDING, PARTIAL, PAID
    private LocalDateTime paymentCompletedAt; // Timestamp when payment was completed

}
