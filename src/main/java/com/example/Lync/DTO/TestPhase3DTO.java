package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestPhase3DTO {

    private Boolean testPassed; // Indicates whether the test passed
    private String failureReason; // Reason for test failure (if any)
    private LocalDateTime resultUploadDate; // Timestamp when the results were uploaded
    private LocalDate dispatchEstimationDate; // Estimated dispatch date (if passed)
    private LocalDate arrivalEstimationDate; // Estimated arrival date (7 days from dispatch)

}
