package com.example.Lync.Repository;

import com.example.Lync.Entity.Variety;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VarietyRepository extends JpaRepository<Variety, Long> {
    Variety findByVarietyName(String varietyName);
    boolean existsByVarietyName(String varietyName);
    Optional<String> findVarietyNameByVarietyId(Long varietyId);

}

