package com.assessment.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category; // JAVA, SQL, ENGLISH

    @Column(nullable = false)
    private String topic; // ARRAYS, STRINGS, DATA_STRUCTURES / JOINS, AGGREGATION, WINDOW_FUNCTIONS / COMMUNICATION

    @Column(nullable = false)
    private String difficulty; // MEDIUM, INTERMEDIATE

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String inputFormat;

    @Column(columnDefinition = "TEXT")
    private String outputFormat;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Column(columnDefinition = "TEXT")
    private String starterCode;

    private int timeLimitMs = 2000;
    private int memoryLimitMb = 128;
    private int maxScore = 10;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCase> testCases = new ArrayList<>();

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private SqlDataset sqlDataset;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Question() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInputFormat() { return inputFormat; }
    public void setInputFormat(String inputFormat) { this.inputFormat = inputFormat; }

    public String getOutputFormat() { return outputFormat; }
    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }

    public String getConstraints() { return constraints; }
    public void setConstraints(String constraints) { this.constraints = constraints; }

    public String getStarterCode() { return starterCode; }
    public void setStarterCode(String starterCode) { this.starterCode = starterCode; }

    public int getTimeLimitMs() { return timeLimitMs; }
    public void setTimeLimitMs(int timeLimitMs) { this.timeLimitMs = timeLimitMs; }

    public int getMemoryLimitMb() { return memoryLimitMb; }
    public void setMemoryLimitMb(int memoryLimitMb) { this.memoryLimitMb = memoryLimitMb; }

    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }

    public List<TestCase> getTestCases() { return testCases; }
    public void setTestCases(List<TestCase> testCases) { this.testCases = testCases; }

    public SqlDataset getSqlDataset() { return sqlDataset; }
    public void setSqlDataset(SqlDataset sqlDataset) { this.sqlDataset = sqlDataset; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
