package com.example.Lync.Repository;


import com.example.Lync.Entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, String> {
    List<Test> findByQueryId(String queryId); // Find all tests by queryId
}


