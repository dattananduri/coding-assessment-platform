package com.assessment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "english_evaluations")
public class EnglishEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    @JsonIgnore
    private ExamAttempt attempt;

    private String audioFilePath;

    @Column(columnDefinition = "TEXT")
    private String transcript;

    private int grammar = 0;
    private int vocabulary = 0;
    private int fluency = 0;
    private int pronunciation = 0;
    private int relevance = 0;
    private int structure = 0;

    private double totalScore = 0.0; // Scaled to 40 points

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private LocalDateTime evaluatedAt = LocalDateTime.now();

    public EnglishEvaluation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ExamAttempt getAttempt() { return attempt; }
    public void setAttempt(ExamAttempt attempt) { this.attempt = attempt; }

    public String getAudioFilePath() { return audioFilePath; }
    public void setAudioFilePath(String audioFilePath) { this.audioFilePath = audioFilePath; }

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }

    public int getGrammar() { return grammar; }
    public void setGrammar(int grammar) { this.grammar = grammar; }

    public int getVocabulary() { return vocabulary; }
    public void setVocabulary(int vocabulary) { this.vocabulary = vocabulary; }

    public int getFluency() { return fluency; }
    public void setFluency(int fluency) { this.fluency = fluency; }

    public int getPronunciation() { return pronunciation; }
    public void setPronunciation(int pronunciation) { this.pronunciation = pronunciation; }

    public int getRelevance() { return relevance; }
    public void setRelevance(int relevance) { this.relevance = relevance; }

    public int getStructure() { return structure; }
    public void setStructure(int structure) { this.structure = structure; }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}
