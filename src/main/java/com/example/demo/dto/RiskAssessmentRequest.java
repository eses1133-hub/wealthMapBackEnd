package com.example.demo.dto;
public record RiskAssessmentRequest(
    Long userId,            
    int ageScore,
    int knowledgeScore,
    int experienceScore,
    int toleranceScore,
    int durationScore,
    int allocationScore
) {
    public int calculateTotalScore() {
        return ageScore + knowledgeScore + experienceScore + 
               toleranceScore + durationScore + allocationScore;
    }
}