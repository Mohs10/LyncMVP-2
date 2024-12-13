package com.example.Lync.Repository;

import com.example.Lync.Entity.SampleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SampleOrderRepository extends JpaRepository<SampleOrder, String> {
    @Query("SELECT COUNT(s) FROM SampleOrder s WHERE s.buyerRequestDate= :currentDate")
    Long countSampleOrderByCurrentDate(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT s FROM SampleOrder s WHERE s.buyerUId = :buyerUId")
    List<SampleOrder> findAllByBuyerUId(@Param("buyerUId") String buyerUId);

    @Query("SELECT s FROM SampleOrder s WHERE s.sellerUId = :sellerUId")
    List<SampleOrder> findAllBySellerUId(@Param("sellerUId") String sellerUId);

    @Query("SELECT so FROM SampleOrder so WHERE so.qId = :qId")
    Optional<SampleOrder> findByQId(@Param("qId") String qId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM SampleOrder s WHERE s.qId = :qId AND s.buyerUId = :buyerUId")
    boolean existsByQIdAndBuyerUId(@Param("qId") String qId, @Param("buyerUId") String buyerUId);


}
