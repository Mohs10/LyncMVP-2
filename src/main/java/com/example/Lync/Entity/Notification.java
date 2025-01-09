package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notification {

    @Id
    private String notificationId;
    private String message;
    private String buyerId;
    private String sellerId;
    private Boolean isRead;
    private LocalDate date;
    private LocalTime time;
}
