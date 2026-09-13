package com.assessment.dto;

public class TestCaseResultDto {
    private int testCaseIndex;
    private boolean isHidden;
    private String input;
    private String expectedOutput;
    private String actualOutput;
    private boolean passed;
    private long executionTimeMs;
    private String error;

    public TestCaseResultDto() {}

    public TestCaseResultDto(int testCaseIndex, boolean isHidden, String input, String expectedOutput, String actualOutput, boolean passed, long executionTimeMs, String error) {
        this.testCaseIndex = testCaseIndex;
        this.isHidden = isHidden;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.actualOutput = actualOutput;
        this.passed = passed;
        this.executionTimeMs = executionTimeMs;
        this.error = error;
    }

    public int getTestCaseIndex() { return testCaseIndex; }
    public void setTestCaseIndex(int testCaseIndex) { this.testCaseIndex = testCaseIndex; }

    public boolean isHidden() { return isHidden; }
    public void setHidden(boolean hidden) { isHidden = hidden; }

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }

    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }

    public String getActualOutput() { return actualOutput; }
    public void setActualOutput(String actualOutput) { this.actualOutput = actualOutput; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
