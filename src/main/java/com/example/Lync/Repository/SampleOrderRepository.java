package com.example.Lync.Repository;

import com.example.Lync.Entity.SampleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SampleOrderRepository extends JpaRepository<SampleOrder, String> {
    @Query("SELECT COUNT(s) FROM SampleOrder s WHERE s.buyerRequestDate= :currentDate")
    Long countSampleOrderByCurrentDate(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT s FROM SampleOrder s WHERE s.buyerUId = :buyerUId")
    List<SampleOrder> findAllByBuyerUId(@Param("buyerUId") String buyerUId);

    @Query("SELECT s FROM SampleOrder s WHERE s.sellerUId = :sellerUId")
    List<SampleOrder> findAllBySellerUId(@Param("sellerUId") String sellerUId);

}
