package com.assessment.dto;

public class TestCaseDto {
    private Long id;
    private String inputData;
    private String expectedOutput;
    private boolean isHidden;

    public TestCaseDto() {}

    public TestCaseDto(Long id, String inputData, String expectedOutput, boolean isHidden) {
        this.id = id;
        this.inputData = inputData;
        this.expectedOutput = expectedOutput;
        this.isHidden = isHidden;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInputData() { return inputData; }
    public void setInputData(String inputData) { this.inputData = inputData; }

    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }

    public boolean isHidden() { return isHidden; }
    public void setHidden(boolean hidden) { isHidden = hidden; }
}
