package com.example.Lync.ServiceImpl;

import com.example.Lync.DTO.NotificationDTO;
import com.example.Lync.Entity.Notification;
import com.example.Lync.Repository.InquiryRepository;
import com.example.Lync.Repository.NotificationRepository;
import com.example.Lync.Repository.SampleOrderRepository;
import com.example.Lync.Repository.TestRepository;
import com.example.Lync.Service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private NotificationRepository notificationRepository;
    private InquiryRepository inquiryRepository;
    private SampleOrderRepository sampleOrderRepository;
    private TestRepository testRepository;


    @Override
    public List<NotificationDTO> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        return notifications.stream().map(
                notification -> {
                    NotificationDTO notificationDTO = new NotificationDTO();
                    notificationDTO.setNotificationId(notification.getNotificationId());
                    notificationDTO.setBuyerId(notification.getBuyerId());
                    notificationDTO.setSellerId(notification.getSellerId());
                    notificationDTO.setMessage(notification.getMessage());
                    notificationDTO.setIsRead(notification.getIsRead());
                    notificationDTO.setDate(notification.getDate());
                    notificationDTO.setTime(notification.getTime());


                    return notificationDTO;
                }
        ).toList();
    }

    @Override
    public String deleteNotificationById(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with given Id: " + notificationId));
        notificationRepository.delete(notification);
        return "Notification deleted successfully.";
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteNotificationOlderThan30Days() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Notification> notificationsToDelete = notificationRepository.findByCreatedAtBefore(thirtyDaysAgo);

        if(!notificationsToDelete.isEmpty()) {
            notificationRepository.deleteAll(notificationsToDelete);
        }
        System.out.println("Deleted " + notificationsToDelete.size() + " notifications older than 30 days.");

    }

    @Override
    public Object notificationDetails(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (notification.getInquiryId() != null) {
            return inquiryRepository.findByQId(notification.getInquiryId())
                    .orElseThrow(() -> new RuntimeException("Inquiry not found with id: " + notification.getInquiryId()));
        }

        if (notification.getSoId() != null) {
            return sampleOrderRepository.findById(notification.getSoId())
                    .orElseThrow(() -> new RuntimeException("Sample Order not found with id: " + notification.getSoId()));
        }

        if (notification.getTestId() != null) {
            return testRepository.findById(notification.getTestId())
                    .orElseThrow(() -> new RuntimeException("Test not found with id: " + notification.getTestId()));
        }

//Order to be added
        throw new RuntimeException("No valid ID found in the notification.");
    }

}