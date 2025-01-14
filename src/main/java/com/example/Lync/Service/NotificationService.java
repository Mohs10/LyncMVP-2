package com.example.Lync.Service;

import com.example.Lync.DTO.NotificationDTO;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> getAllNotifications();

    String deleteNotificationById(String notificationId);

    void deleteNotificationOlderThan30Days();

    Object notificationDetails(String notificationId);


}
