package com.assessment.dto;

import java.util.ArrayList;
import java.util.List;

public class AdminQuestionDto {
    private Long id;
    private String category;
    private String topic;
    private String difficulty;
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private String constraints;
    private String starterCode;
    private int timeLimitMs = 2000;
    private int memoryLimitMb = 128;
    private int maxScore = 10;
    private List<TestCaseDto> testCases = new ArrayList<>();
    
    // SQL specific
    private String schemaDescription;
    private String schemaDdl;
    private String seedDataSql;
    private String referenceQuery;
    private boolean orderRequired;
    private String sampleOutputJson;

    public AdminQuestionDto() {}

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

    public List<TestCaseDto> getTestCases() { return testCases; }
    public void setTestCases(List<TestCaseDto> testCases) { this.testCases = testCases; }

    public String getSchemaDescription() { return schemaDescription; }
    public void setSchemaDescription(String schemaDescription) { this.schemaDescription = schemaDescription; }

    public String getSchemaDdl() { return schemaDdl; }
    public void setSchemaDdl(String schemaDdl) { this.schemaDdl = schemaDdl; }

    public String getSeedDataSql() { return seedDataSql; }
    public void setSeedDataSql(String seedDataSql) { this.seedDataSql = seedDataSql; }

    public String getReferenceQuery() { return referenceQuery; }
    public void setReferenceQuery(String referenceQuery) { this.referenceQuery = referenceQuery; }

    public boolean isOrderRequired() { return orderRequired; }
    public void setOrderRequired(boolean orderRequired) { this.orderRequired = orderRequired; }

    public String getSampleOutputJson() { return sampleOutputJson; }
    public void setSampleOutputJson(String sampleOutputJson) { this.sampleOutputJson = sampleOutputJson; }
}
