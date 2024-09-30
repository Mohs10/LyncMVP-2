package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
public class Test {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String testId;
    private String requestedBy; // BuyerId
    private LocalDate testDate;
    private String testReportUrl;

    private String testType;
    private String testStatus; // PENDING, IN_PROGRESS, COMPLETED
    private LocalDateTime requestedAt;
//    private String testResultDetails;
    private String testedBy; // Authority or lab conducting the test
    private Double testCost;
    private Boolean isApproved;
 //    private String attachmentUrl;
//    private Duration testDuration;

    // Getters and Setters
}


