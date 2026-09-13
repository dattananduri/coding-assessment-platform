package com.assessment.dto;

import java.util.List;

public class QuestionDetailDto {
    private int order;
    private Long questionId;
    private String category;
    private String topic;
    private String difficulty;
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private String constraints;
    private String starterCode;
    private String currentCode;
    private String status;
    private int maxScore;
    private double scoreAwarded;
    private int timeLimitMs;
    private int memoryLimitMb;
    private List<TestCaseDto> sampleTestCases;

    // SQL specific
    private String sqlSchemaDescription;
    private String sqlSampleOutput;

    // English specific
    private String audioFilePath;
    private String transcript;
    private EnglishSubmitResponseDto englishEvaluation;

    public QuestionDetailDto() {}

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

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

    public String getCurrentCode() { return currentCode; }
    public void setCurrentCode(String currentCode) { this.currentCode = currentCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }

    public double getScoreAwarded() { return scoreAwarded; }
    public void setScoreAwarded(double scoreAwarded) { this.scoreAwarded = scoreAwarded; }

    public int getTimeLimitMs() { return timeLimitMs; }
    public void setTimeLimitMs(int timeLimitMs) { this.timeLimitMs = timeLimitMs; }

    public int getMemoryLimitMb() { return memoryLimitMb; }
    public void setMemoryLimitMb(int memoryLimitMb) { this.memoryLimitMb = memoryLimitMb; }

    public List<TestCaseDto> getSampleTestCases() { return sampleTestCases; }
    public void setSampleTestCases(List<TestCaseDto> sampleTestCases) { this.sampleTestCases = sampleTestCases; }

    public String getSqlSchemaDescription() { return sqlSchemaDescription; }
    public void setSqlSchemaDescription(String sqlSchemaDescription) { this.sqlSchemaDescription = sqlSchemaDescription; }

    public String getSqlSampleOutput() { return sqlSampleOutput; }
    public void setSqlSampleOutput(String sqlSampleOutput) { this.sqlSampleOutput = sqlSampleOutput; }

    public String getAudioFilePath() { return audioFilePath; }
    public void setAudioFilePath(String audioFilePath) { this.audioFilePath = audioFilePath; }

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }

    public EnglishSubmitResponseDto getEnglishEvaluation() { return englishEvaluation; }
    public void setEnglishEvaluation(EnglishSubmitResponseDto englishEvaluation) { this.englishEvaluation = englishEvaluation; }
}
