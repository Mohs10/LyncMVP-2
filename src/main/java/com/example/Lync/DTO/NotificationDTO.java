package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class    NotificationDTO {
    private String notificationId;
    private String message;
    private String buyerId;
    private String sellerId;
    private Boolean isRead;
    private LocalDate date;
    private LocalTime time;
}
