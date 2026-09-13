package com.assessment.repository;

import com.assessment.model.EnglishEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnglishEvaluationRepository extends JpaRepository<EnglishEvaluation, Long> {
    Optional<EnglishEvaluation> findByAttemptId(Long attemptId);
}
