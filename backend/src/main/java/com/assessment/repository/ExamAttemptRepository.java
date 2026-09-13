package com.assessment.repository;

import com.assessment.model.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByTestCodeOrderByStartedAtDesc(String testCode);
    List<ExamAttempt> findAllByOrderByStartedAtDesc();
    Optional<ExamAttempt> findByCandidateEmailAndTestCode(String candidateEmail, String testCode);
}
