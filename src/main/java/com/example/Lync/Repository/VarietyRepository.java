package com.example.Lync.Repository;

import com.example.Lync.Entity.Variety;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VarietyRepository extends JpaRepository<Variety, Long> {
    Variety findByVarietyName(String varietyName);
    boolean existsByVarietyName(String varietyName);
    @Query("SELECT v.varietyName FROM Variety v WHERE v.varietyId = :varietyId")
    Optional<String> findVarietyNameByVarietyId(@Param("varietyId") Long varietyId);
}

