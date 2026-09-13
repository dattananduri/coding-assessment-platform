package com.assessment.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_attempts")
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String candidateName;

    @Column(nullable = false)
    private String candidateEmail;

    @Column(nullable = false)
    private String testCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime submittedAt;

    @Column(nullable = false)
    private String status = "IN_PROGRESS"; // IN_PROGRESS, SUBMITTED, TERMINATED_VIOLATION

    private double javaScore = 0.0;
    private double sqlScore = 0.0;
    private double englishScore = 0.0;
    private double totalScore = 0.0;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionOrder ASC")
    @JsonManagedReference
    private List<AttemptQuestion> attemptQuestions = new ArrayList<>();

    @OneToOne(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private EnglishEvaluation englishEvaluation;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp ASC")
    private List<ViolationLog> violations = new ArrayList<>();

    public ExamAttempt() {}

    public ExamAttempt(String candidateName, String candidateEmail, String testCode, Assessment assessment, LocalDateTime startedAt, LocalDateTime expiresAt) {
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.testCode = testCode;
        this.assessment = assessment;
        this.startedAt = startedAt;
        this.expiresAt = expiresAt;
        this.status = "IN_PROGRESS";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }

    public String getTestCode() { return testCode; }
    public void setTestCode(String testCode) { this.testCode = testCode; }

    public Assessment getAssessment() { return assessment; }
    public void setAssessment(Assessment assessment) { this.assessment = assessment; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getJavaScore() { return javaScore; }
    public void setJavaScore(double javaScore) { this.javaScore = javaScore; }

    public double getSqlScore() { return sqlScore; }
    public void setSqlScore(double sqlScore) { this.sqlScore = sqlScore; }

    public double getEnglishScore() { return englishScore; }
    public void setEnglishScore(double englishScore) { this.englishScore = englishScore; }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    public List<AttemptQuestion> getAttemptQuestions() { return attemptQuestions; }
    public void setAttemptQuestions(List<AttemptQuestion> attemptQuestions) { this.attemptQuestions = attemptQuestions; }

    public EnglishEvaluation getEnglishEvaluation() { return englishEvaluation; }
    public void setEnglishEvaluation(EnglishEvaluation englishEvaluation) { this.englishEvaluation = englishEvaluation; }

    public List<ViolationLog> getViolations() { return violations; }
    public void setViolations(List<ViolationLog> violations) { this.violations = violations; }
}
