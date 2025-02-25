package com.example.Lync.Repository;

import com.example.Lync.Entity.SellerNegotiate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SellerNegotiateRepository extends JpaRepository<SellerNegotiate, Long> {

    @Query("SELECT sn FROM SellerNegotiate sn WHERE sn.qId = :qId ORDER BY sn.aipDate DESC, sn.aipTime DESC")
    List<SellerNegotiate> findByQId(@Param("qId") String qId);

    @Query("SELECT sn FROM SellerNegotiate sn WHERE sn.sellerUId = :sellerUId ORDER BY sn.aipDate DESC, sn.aipTime DESC")
    List<SellerNegotiate> findBySellerUId(@Param("sellerUId") String sellerUId);
}
// ORDER BY sn.aipDate DESC, sn.aipTime DESC
