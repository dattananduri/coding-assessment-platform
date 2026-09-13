package com.assessment.dto;

import java.util.ArrayList;
import java.util.List;

public class SqlExecutionResultDto {
    private boolean passed;
    private String status; // ACCEPTED, WRONG_ANSWER, SYNTAX_ERROR, SECURITY_VIOLATION
    private long executionTimeMs;
    private double scoreAwarded;
    private List<String> candidateColumns = new ArrayList<>();
    private List<List<Object>> candidateRows = new ArrayList<>();
    private List<String> expectedColumns = new ArrayList<>();
    private List<List<Object>> expectedRows = new ArrayList<>();
    private String message;
    private String error;

    public SqlExecutionResultDto() {}

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public double getScoreAwarded() { return scoreAwarded; }
    public void setScoreAwarded(double scoreAwarded) { this.scoreAwarded = scoreAwarded; }

    public List<String> getCandidateColumns() { return candidateColumns; }
    public void setCandidateColumns(List<String> candidateColumns) { this.candidateColumns = candidateColumns; }

    public List<List<Object>> getCandidateRows() { return candidateRows; }
    public void setCandidateRows(List<List<Object>> candidateRows) { this.candidateRows = candidateRows; }

    public List<String> getExpectedColumns() { return expectedColumns; }
    public void setExpectedColumns(List<String> expectedColumns) { this.expectedColumns = expectedColumns; }

    public List<List<Object>> getExpectedRows() { return expectedRows; }
    public void setExpectedRows(List<List<Object>> expectedRows) { this.expectedRows = expectedRows; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
