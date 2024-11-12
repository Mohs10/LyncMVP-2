package com.example.Lync.Repository;

import com.example.Lync.Entity.SellerProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerProductSpecificationRepository extends JpaRepository<SellerProductSpecification, Long> {
    // You can add custom query methods here if needed
}
