package com.assessment.dto;

public class CandidateStartRequest {
    private String candidateName;
    private String candidateEmail;
    private String testCode;

    public CandidateStartRequest() {}

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }

    public String getTestCode() { return testCode; }
    public void setTestCode(String testCode) { this.testCode = testCode; }
}
