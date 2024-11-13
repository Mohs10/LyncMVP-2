package com.example.Lync.Repository;

import com.example.Lync.Entity.BuyerNegotiate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BuyerNegotiateRepository extends JpaRepository<BuyerNegotiate, Long> {

    @Query("SELECT b FROM BuyerNegotiate b WHERE b.qId = :qId")
    BuyerNegotiate findByQId(@Param("qId") String qId);
}
