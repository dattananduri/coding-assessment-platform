package com.assessment.repository;

import com.assessment.model.SqlDataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SqlDatasetRepository extends JpaRepository<SqlDataset, Long> {
    Optional<SqlDataset> findByQuestionId(Long questionId);
}
