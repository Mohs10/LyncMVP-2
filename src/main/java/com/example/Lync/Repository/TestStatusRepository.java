package com.example.Lync.Repository;

import com.example.Lync.Entity.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestStatusRepository extends JpaRepository<TestStatus, String> {

    /**
     * Find all TestStatus entries associated with a specific Test ID.
     *
     * @param testId the ID of the test
     * @return a list of TestStatus objects
     */
    List<TestStatus> findByTestId(String testId);
}
