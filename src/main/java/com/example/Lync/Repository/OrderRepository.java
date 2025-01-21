package com.example.Lync.Repository;

import com.example.Lync.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT COUNT(o) FROM Order o WHERE o.buyerPurchaseOrderURLDate = :currentDate")
    Long countOrderByCurrentDate(@Param("currentDate")LocalDate currentDate);
}
