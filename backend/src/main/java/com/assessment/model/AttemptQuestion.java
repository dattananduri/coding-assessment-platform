package com.assessment.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "attempt_questions")
public class AttemptQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    @JsonBackReference
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    private int questionOrder; // 1 to 7

    @Column(nullable = false)
    private String status = "NOT_ATTEMPTED"; // NOT_ATTEMPTED, IN_PROGRESS, SUBMITTED, PASSED, FAILED

    @Column(columnDefinition = "TEXT")
    private String currentCode;

    private double scoreAwarded = 0.0;

    private int passedTestCases = 0;
    private int totalTestCases = 0;

    @Column(columnDefinition = "TEXT")
    private String lastRunOutput;

    public AttemptQuestion() {}

    public AttemptQuestion(ExamAttempt attempt, Question question, int questionOrder, String currentCode) {
        this.attempt = attempt;
        this.question = question;
        this.questionOrder = questionOrder;
        this.currentCode = currentCode;
        this.status = "NOT_ATTEMPTED";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ExamAttempt getAttempt() { return attempt; }
    public void setAttempt(ExamAttempt attempt) { this.attempt = attempt; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public int getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(int questionOrder) { this.questionOrder = questionOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentCode() { return currentCode; }
    public void setCurrentCode(String currentCode) { this.currentCode = currentCode; }

    public double getScoreAwarded() { return scoreAwarded; }
    public void setScoreAwarded(double scoreAwarded) { this.scoreAwarded = scoreAwarded; }

    public int getPassedTestCases() { return passedTestCases; }
    public void setPassedTestCases(int passedTestCases) { this.passedTestCases = passedTestCases; }

    public int getTotalTestCases() { return totalTestCases; }
    public void setTotalTestCases(int totalTestCases) { this.totalTestCases = totalTestCases; }

    public String getLastRunOutput() { return lastRunOutput; }
    public void setLastRunOutput(String lastRunOutput) { this.lastRunOutput = lastRunOutput; }
}
