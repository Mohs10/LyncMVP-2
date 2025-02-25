package com.example.Lync.Repository;

import com.example.Lync.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    void deleteByDateBefore(LocalDate date);

    List<Notification> findByCreatedAtBefore(LocalDateTime date);

    @Query("SELECT n FROM Notification n WHERE n.isAdmin = true ORDER BY n.date DESC, n.time DESC")
    List<Notification> findAllByIsAdminTrue();

    @Query("SELECT n FROM Notification n WHERE n.sellerId IS NOT NULL ORDER BY n.date DESC, n.time DESC")
    List<Notification> findAllWhereSellerIdIsNotNull();

    @Query("SELECT n FROM Notification n WHERE n.buyerId IS NOT NULL ORDER BY n.date DESC, n.time DESC")
    List<Notification> findAllWhereBuyerIdIsNotNull();

}
