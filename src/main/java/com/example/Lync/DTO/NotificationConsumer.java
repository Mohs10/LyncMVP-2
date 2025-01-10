package com.example.Lync.DTO;

import com.example.Lync.Config.MessageConfig;
import com.example.Lync.Entity.Notification;
import com.example.Lync.Repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

//@Component
//@AllArgsConstructor
//public class NotificationConsumer {
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    @Autowired
//    private NotificationRepository notificationRepository;
//
//    @RabbitListener(queues = "notification.queue")
//    public void consumeNotification(Notification notification) {
//        // Save notification to the database
//        notificationRepository.save(notification);
//
//        messagingTemplate.convertAndSend("/topic/notifications", notification);
//        System.out.println("Notification received and saved: " + notification);
//    }
//}
