package com.example.Lync.Repository;

import com.example.Lync.Entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StatusRepository extends JpaRepository<Status, Long> {

    @Query("SELECT s.sMeaning FROM Status s WHERE s.sId = :sId")
    String findSMeaningBySId(Long sId);
}
