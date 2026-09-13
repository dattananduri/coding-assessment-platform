package com.assessment.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FinalResultDto {
    private Long attemptId;
    private String candidateName;
    private String candidateEmail;
    private String testCode;
    private String assessmentTitle;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private double javaScore;
    private double sqlScore;
    private double englishScore;
    private double totalScore;
    private double maxScore = 100.0;
    private int violationsCount;
    private List<QuestionSummaryDto> questionSummaries = new ArrayList<>();
    private EnglishSubmitResponseDto englishEvaluation;

    public FinalResultDto() {}

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

    public int getViolationsCount() { return violationsCount; }
    public void setViolationsCount(int violationsCount) { this.violationsCount = violationsCount; }

    public List<QuestionSummaryDto> getQuestionSummaries() { return questionSummaries; }
    public void setQuestionSummaries(List<QuestionSummaryDto> questionSummaries) { this.questionSummaries = questionSummaries; }

    public EnglishSubmitResponseDto getEnglishEvaluation() { return englishEvaluation; }
    public void setEnglishEvaluation(EnglishSubmitResponseDto englishEvaluation) { this.englishEvaluation = englishEvaluation; }
}
