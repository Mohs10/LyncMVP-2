package com.example.Lync.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
public class TestStatus {

    // Primary Identifier
    @Id
    private String statusId; // Unique identifier for the status

    // Test Identifier
    private String testId; // Associated Test ID

    // Status Information
    private String statusPhase; // Phase of the test lifecycle (e.g., "Sampling", "Testing", "Dispatch", etc.)
    private String statusMessage; // Detailed message about the current status

    private boolean visibleToSeller; // Flag to indicate if the status is visible to the seller
    private boolean visibleToBuyer; // Flag to indicate if the status is visible to the buyer

    // Timestamps
    private LocalDateTime statusTimestamp; // Timestamp when this status was updated

    // Additional Details
    @Column(columnDefinition = "TEXT")
    private String additionalDetails; // Optional field for storing any extra information about the status
}
