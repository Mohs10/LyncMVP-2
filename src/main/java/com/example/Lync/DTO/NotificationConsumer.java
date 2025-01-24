package com.example.Lync.DTO;

import com.example.Lync.Config.MessageConfig;
import com.example.Lync.Entity.Notification;
import com.example.Lync.Repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotificationConsumer {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

//    @RabbitListener(queues = "notification.queue")
//    public void consumeNotification(Notification notification) {
//        // Save notification to the database
////        notificationRepository.save(notification);
//
//        messagingTemplate.convertAndSend("/topic/notifications", notification);
////        System.out.println("Notification received and saved: " + notification);
//    }
//
//    // Listener for Buyer Notifications
//    @RabbitListener(queues = MessageConfig.BUYER_QUEUE)
//    public void handleBuyerNotification(Notification notification) {
//        String destination = "/topic/notifications/buyer/" + notification.getBuyerId();
//        messagingTemplate.convertAndSend(destination, notification);
////        logNotification("Buyer", notification, destination);
//    }
//
//    // Listener for Seller Notifications
//    @RabbitListener(queues = MessageConfig.SELLER_QUEUE)
//    public void handleSellerNotification(Notification notification) {
//        String destination = "/topic/notifications/seller/" + notification.getSellerId();
//        messagingTemplate.convertAndSend(destination, notification);
////        logNotification("Seller", notification, destination);
//    }
//
//    // Listener for Admin Notifications
//    @RabbitListener(queues = MessageConfig.ADMIN_QUEUE)
//    public void handleAdminNotification(Notification notification) {
//        String destination = "/topic/notifications";
//        messagingTemplate.convertAndSend(destination, notification);
////        logNotification("Admin", notification, destination);
//    }

    // Listener for Default Notifications
//    @RabbitListener(queues = MessageConfig.DEFAULT_QUEUE)
//    public void handleDefaultNotification(Notification notification) {
//        String destination = "/topic/notifications/general";
//        messagingTemplate.convertAndSend(destination, notification);
//        logNotification("General", notification, destination);
//    }
}
