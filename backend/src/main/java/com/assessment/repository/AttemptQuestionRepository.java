package com.assessment.repository;

import com.assessment.model.AttemptQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttemptQuestionRepository extends JpaRepository<AttemptQuestion, Long> {
    List<AttemptQuestion> findByAttemptIdOrderByQuestionOrderAsc(Long attemptId);
    Optional<AttemptQuestion> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);
    Optional<AttemptQuestion> findByAttemptIdAndQuestionOrder(Long attemptId, int questionOrder);
}
