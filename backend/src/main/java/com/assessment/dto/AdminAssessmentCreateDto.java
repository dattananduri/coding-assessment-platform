package com.assessment.dto;

public class AdminAssessmentCreateDto {
    private String title;
    private String code;
    private String description;
    private int durationMinutes = 90;

    public AdminAssessmentCreateDto() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
}
