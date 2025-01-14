package com.example.Lync.Repository;

import com.example.Lync.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    void deleteByDateBefore(LocalDate date);

    List<Notification> findByCreatedAtBefore(LocalDateTime date);
}
