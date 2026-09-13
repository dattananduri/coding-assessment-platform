package com.assessment.dto;

public class QuestionSummaryDto {
    private int order;
    private Long questionId;
    private String category;
    private String topic;
    private String title;
    private String status;
    private int maxScore;
    private double scoreAwarded;
    private int passedTestCases;
    private int totalTestCases;

    public QuestionSummaryDto() {}

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }

    public double getScoreAwarded() { return scoreAwarded; }
    public void setScoreAwarded(double scoreAwarded) { this.scoreAwarded = scoreAwarded; }

    public int getPassedTestCases() { return passedTestCases; }
    public void setPassedTestCases(int passedTestCases) { this.passedTestCases = passedTestCases; }

    public int getTotalTestCases() { return totalTestCases; }
    public void setTotalTestCases(int totalTestCases) { this.totalTestCases = totalTestCases; }
}
