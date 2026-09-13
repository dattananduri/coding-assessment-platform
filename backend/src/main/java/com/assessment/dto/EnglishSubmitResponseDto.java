package com.assessment.dto;

public class EnglishSubmitResponseDto {
    private int grammar;
    private int vocabulary;
    private int fluency;
    private int pronunciation;
    private int relevance;
    private int structure;
    private double total; // scaled to 40
    private String feedback;
    private String transcript;

    public EnglishSubmitResponseDto() {}

    public EnglishSubmitResponseDto(int grammar, int vocabulary, int fluency, int pronunciation, int relevance, int structure, double total, String feedback, String transcript) {
        this.grammar = grammar;
        this.vocabulary = vocabulary;
        this.fluency = fluency;
        this.pronunciation = pronunciation;
        this.relevance = relevance;
        this.structure = structure;
        this.total = total;
        this.feedback = feedback;
        this.transcript = transcript;
    }

    public int getGrammar() { return grammar; }
    public void setGrammar(int grammar) { this.grammar = grammar; }

    public int getVocabulary() { return vocabulary; }
    public void setVocabulary(int vocabulary) { this.vocabulary = vocabulary; }

    public int getFluency() { return fluency; }
    public void setFluency(int fluency) { this.fluency = fluency; }

    public int getPronunciation() { return pronunciation; }
    public void setPronunciation(int pronunciation) { this.pronunciation = pronunciation; }

    public int getRelevance() { return relevance; }
    public void setRelevance(int relevance) { this.relevance = relevance; }

    public int getStructure() { return structure; }
    public void setStructure(int structure) { this.structure = structure; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }
}
