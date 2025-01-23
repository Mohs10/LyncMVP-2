package com.example.Lync.Service;

import com.example.Lync.DTO.NotificationDTO;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> getAllNotifications();

    String adminDeleteNotificationById(String notificationId);

    String buyerDeleteNotificationById(String notificationId, String buyerId);

    String sellerDeleteNotificationById(String notificationId, String sellerId);

    void deleteNotificationOlderThan30Days();

    Object notificationDetails(String notificationId);

    List<NotificationDTO> buyerGetAllNotification(String buyerId);

    List<NotificationDTO> sellerGetAllNotification(String sellerId);

    List<NotificationDTO> adminGetAllNotification();


}
