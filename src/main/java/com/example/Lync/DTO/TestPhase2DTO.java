package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestPhase2DTO {
    private LocalDate samplingDate; // Actual sampling date (when testing started by 3rd party)
    private String samplingLocation; // Location of the lot
    private LocalDate estimatedResultDate; // Estimated result date (after 10 days)

}
