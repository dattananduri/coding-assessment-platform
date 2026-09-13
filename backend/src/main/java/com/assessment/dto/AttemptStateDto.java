package com.assessment.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AttemptStateDto {
    private Long attemptId;
    private String candidateName;
    private String candidateEmail;
    private String testCode;
    private String assessmentTitle;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private long remainingSeconds;
    private double currentTotalScore;
    private List<QuestionSummaryDto> questions;

    public AttemptStateDto() {}

    public Long getAttemptId() { return attemptId; }
    public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }

    public String getTestCode() { return testCode; }
    public void setTestCode(String testCode) { this.testCode = testCode; }

    public String getAssessmentTitle() { return assessmentTitle; }
    public void setAssessmentTitle(String assessmentTitle) { this.assessmentTitle = assessmentTitle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public long getRemainingSeconds() { return remainingSeconds; }
    public void setRemainingSeconds(long remainingSeconds) { this.remainingSeconds = remainingSeconds; }

    public double getCurrentTotalScore() { return currentTotalScore; }
    public void setCurrentTotalScore(double currentTotalScore) { this.currentTotalScore = currentTotalScore; }

    public List<QuestionSummaryDto> getQuestions() { return questions; }
    public void setQuestions(List<QuestionSummaryDto> questions) { this.questions = questions; }
}
