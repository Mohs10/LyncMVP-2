package com.example.Lync.Repository;

import com.example.Lync.Entity.StandardOperatingProcedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StandardOperatingProcedureRepository extends JpaRepository<StandardOperatingProcedure, Long> {

    Optional<StandardOperatingProcedure> findByForRole(String forRole);

}

