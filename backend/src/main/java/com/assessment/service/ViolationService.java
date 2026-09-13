package com.assessment.service;

import com.assessment.dto.ViolationReportRequest;
import com.assessment.model.ExamAttempt;
import com.assessment.model.ViolationLog;
import com.assessment.repository.ExamAttemptRepository;
import com.assessment.repository.ViolationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ViolationService {

    private final ViolationLogRepository violationLogRepository;
    private final ExamAttemptRepository examAttemptRepository;

    public ViolationService(ViolationLogRepository violationLogRepository, ExamAttemptRepository examAttemptRepository) {
        this.violationLogRepository = violationLogRepository;
        this.examAttemptRepository = examAttemptRepository;
    }

    @Transactional
    public ViolationLog recordViolation(Long attemptId, ViolationReportRequest request, boolean terminateAttempt) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        ViolationLog log = new ViolationLog(attempt, request.getViolationType(), request.getDetails());
        log = violationLogRepository.save(log);

        if (terminateAttempt && "IN_PROGRESS".equals(attempt.getStatus())) {
            attempt.setStatus("TERMINATED_VIOLATION");
            attempt.setSubmittedAt(LocalDateTime.now());
            examAttemptRepository.save(attempt);
        }

        return log;
    }
}
