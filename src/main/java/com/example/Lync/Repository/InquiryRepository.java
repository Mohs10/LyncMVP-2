package com.example.Lync.Repository;

import com.example.Lync.Entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, String> {

    @Query("SELECT COUNT(i) FROM Inquiry i WHERE i.raiseDate= :currentDate")
    Long countInquiryByCurrentDate(@Param("currentDate")LocalDate currentDate);

    @Query("SELECT i FROM Inquiry i WHERE i.qId= :qId")
    Optional<Inquiry> findByQId(String qId);

    @Query("SELECT i FROM Inquiry i WHERE i.buyerId = :buyerId ORDER BY i.raiseDate DESC")
    List<Inquiry> findByBuyerId(@Param("buyerId") String buyerUId);

    @Query("SELECT i FROM Inquiry i ORDER BY i.raiseDate DESC, i.raiseTime DESC")
    Page<Inquiry> findAllInquiriesSorted(Pageable pageable);

}
