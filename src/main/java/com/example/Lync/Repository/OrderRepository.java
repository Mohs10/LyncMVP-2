package com.example.Lync.Repository;

import com.example.Lync.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT COUNT(o) FROM Order o WHERE o.buyerPurchaseOrderURLDate = :currentDate")
    Long countOrderByCurrentDate(@Param("currentDate")LocalDate currentDate);

    @Query("SELECT o.oId FROM Order o WHERE o.qId = :qId")
    String findOIdByQId(String qId);

    List<Order> findByBuyerUId(String buyerUId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.buyerUId = :buyerUId AND FUNCTION('YEAR', o.buyerPurchaseOrderURLDate) = :year AND FUNCTION('MONTH', o.buyerPurchaseOrderURLDate) = :month")
    Long countOrdersByBuyerInMonthAndYear(@Param("buyerUId") String buyerUId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT (o) FROM Order o WHERE o.sellerUId = :sellerUId AND FUNCTION('YEAR', o.buyerPurchaseOrderURLDate) = :year AND FUNCTION('MONTH', o.buyerPurchaseOrderURLDate) = :month")
    Long countOrdersBySellerInMonthAndYear(@Param("sellerUId") String sellerUId, @Param("year") int year, @Param("month") int month);

}
