package com.assessment.dto;

import java.util.ArrayList;
import java.util.List;

public class ExecutionResultDto {
    private String status; // ACCEPTED, WRONG_ANSWER, TIME_LIMIT_EXCEEDED, MEMORY_LIMIT_EXCEEDED, COMPILATION_ERROR, RUNTIME_ERROR, SECURITY_VIOLATION
    private boolean passed;
    private long executionTimeMs;
    private int passedTestCases;
    private int totalTestCases;
    private double scoreAwarded;
    private String output;
    private String error;
    private List<TestCaseResultDto> testCaseResults = new ArrayList<>();

    public ExecutionResultDto() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public int getPassedTestCases() { return passedTestCases; }
    public void setPassedTestCases(int passedTestCases) { this.passedTestCases = passedTestCases; }

    public int getTotalTestCases() { return totalTestCases; }
    public void setTotalTestCases(int totalTestCases) { this.totalTestCases = totalTestCases; }

    public double getScoreAwarded() { return scoreAwarded; }
    public void setScoreAwarded(double scoreAwarded) { this.scoreAwarded = scoreAwarded; }

    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public List<TestCaseResultDto> getTestCaseResults() { return testCaseResults; }
    public void setTestCaseResults(List<TestCaseResultDto> testCaseResults) { this.testCaseResults = testCaseResults; }
}
