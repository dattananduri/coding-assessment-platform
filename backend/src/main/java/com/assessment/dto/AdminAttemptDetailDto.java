package com.assessment.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminAttemptDetailDto {
    private Long attemptId;
    private String candidateName;
    private String candidateEmail;
    private String testCode;
    private String assessmentTitle;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime submittedAt;
    private double javaScore;
    private double sqlScore;
    private double englishScore;
    private double totalScore;
    private double maxScore = 100.0;
    private List<AttemptQuestionDetailDto> questions = new ArrayList<>();
    private EnglishSubmitResponseDto englishEvaluation;
    private String englishAudioUrl;
    private List<ViolationDto> violations = new ArrayList<>();

    public AdminAttemptDetailDto() {}

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

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public double getJavaScore() { return javaScore; }
    public void setJavaScore(double javaScore) { this.javaScore = javaScore; }

    public double getSqlScore() { return sqlScore; }
    public void setSqlScore(double sqlScore) { this.sqlScore = sqlScore; }

    public double getEnglishScore() { return englishScore; }
    public void setEnglishScore(double englishScore) { this.englishScore = englishScore; }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    public double getMaxScore() { return maxScore; }
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }

    public List<AttemptQuestionDetailDto> getQuestions() { return questions; }
    public void setQuestions(List<AttemptQuestionDetailDto> questions) { this.questions = questions; }

    public EnglishSubmitResponseDto getEnglishEvaluation() { return englishEvaluation; }
    public void setEnglishEvaluation(EnglishSubmitResponseDto englishEvaluation) { this.englishEvaluation = englishEvaluation; }

    public String getEnglishAudioUrl() { return englishAudioUrl; }
    public void setEnglishAudioUrl(String englishAudioUrl) { this.englishAudioUrl = englishAudioUrl; }

    public List<ViolationDto> getViolations() { return violations; }
    public void setViolations(List<ViolationDto> violations) { this.violations = violations; }
}
