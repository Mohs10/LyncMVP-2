package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

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
    private Double sOQuantity;
    private LocalDate sODate = LocalDate.now();
    private LocalTime sOTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
    private String stockLocation;
    private String deliveryLocation;
}
