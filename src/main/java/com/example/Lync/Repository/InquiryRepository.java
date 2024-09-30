package com.example.Lync.Repository;

import com.example.Lync.Entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, String> {

    @Query("SELECT COUNT(i) FROM Inquiry i WHERE i.raiseDate= :currentDate")
    Long countInquiryByCurrentDate(@Param("currentDate")LocalDate currentDate);

    @Query("SELECT i FROM Inquiry i WHERE i.qId= :qId")
    Inquiry findByQId(String qId);
}
