package com.example.Lync.Repository;

import com.example.Lync.Entity.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecificationRepository extends JpaRepository<Specification, Long> {
    // You can add custom queries if needed
}

