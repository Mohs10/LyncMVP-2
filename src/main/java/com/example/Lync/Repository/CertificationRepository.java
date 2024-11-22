package com.example.Lync.Repository;

import com.example.Lync.Entity.Certification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    Certification findByCertificationName(String certificationName);
    boolean existsByCertificationName(String certificationName);
    @Modifying
    @Query("DELETE FROM Product p JOIN p.certifications pc WHERE p.productId = :productId")
    void deleteAllByProductId(Long productId);
}

