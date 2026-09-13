package com.assessment.dto;

public class ViolationReportRequest {
    private String violationType;
    private String details;

    public ViolationReportRequest() {}

    public String getViolationType() { return violationType; }
    public void setViolationType(String violationType) { this.violationType = violationType; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
