package com.example.Lync.Repository;

import com.example.Lync.Entity.InquirySpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InquirySpecificationRepository extends JpaRepository<InquirySpecification, Long> {

    @Query("SELECT i FROM InquirySpecification i WHERE i.qId= :qId")
    List<InquirySpecification> findByQId(@Param("qId") String qId);
}
