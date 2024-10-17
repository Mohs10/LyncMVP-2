package com.example.Lync.Repository;

import com.example.Lync.Entity.SellerBuyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerBuyerRepository extends JpaRepository<SellerBuyer, String> {
    Optional<SellerBuyer> findByEmail(String email);

}

