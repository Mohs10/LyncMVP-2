package com.example.Lync.Repository;

import com.example.Lync.Entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    Certification findByCertificationName(String certificationName);
    boolean existsByCertificationName(String certificationName);
}

