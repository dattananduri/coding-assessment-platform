package com.assessment.repository;

import com.assessment.model.ViolationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationLogRepository extends JpaRepository<ViolationLog, Long> {
    List<ViolationLog> findByAttemptIdOrderByTimestampAsc(Long attemptId);
    long countByAttemptId(Long attemptId);
}
