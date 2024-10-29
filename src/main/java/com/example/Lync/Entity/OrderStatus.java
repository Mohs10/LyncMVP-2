package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long osId;
    private String oId; // Reference to orderId
    private String status;
    private LocalDate date= LocalDate.now();
    private LocalTime time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
    private String description;
    private String imageUrl;
    private String location;

    // Getters and Setters
}

