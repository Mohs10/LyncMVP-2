package com.example.Lync.Repository;

import com.example.Lync.Entity.SellerBuyerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SellerBuyerAddressRepository extends JpaRepository<SellerBuyerAddress, Long> {

    @Query("SELECT s FROM SellerBuyerAddress s WHERE s.uId = :uId")
    List<SellerBuyerAddress> findByUId(@Param("uId") String uId);
}
