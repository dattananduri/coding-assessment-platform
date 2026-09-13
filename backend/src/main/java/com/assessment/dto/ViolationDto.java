package com.assessment.dto;

import java.time.LocalDateTime;

public class ViolationDto {
    private Long id;
    private String violationType;
    private LocalDateTime timestamp;
    private String details;

    public ViolationDto() {}

    public ViolationDto(Long id, String violationType, LocalDateTime timestamp, String details) {
        this.id = id;
        this.violationType = violationType;
        this.timestamp = timestamp;
        this.details = details;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getViolationType() { return violationType; }
    public void setViolationType(String violationType) { this.violationType = violationType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
