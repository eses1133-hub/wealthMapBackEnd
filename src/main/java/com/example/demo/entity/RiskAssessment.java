package com.example.demo.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer ageScore;
    private Integer allocationScore;
    private Integer durationScore;
    private Integer experienceScore;
    private Integer knowledgeScore;
    private Integer toleranceScore;

    private Integer totalScore;
    private String riskLevel; 

    private LocalDateTime createdAt = LocalDateTime.now(); 

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore 
    private User user;

    // --- 自動產生 Getters 和 Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getAgeScore() { return ageScore; }
    public void setAgeScore(Integer ageScore) { this.ageScore = ageScore; }
    public Integer getAllocationScore() { return allocationScore; }
    public void setAllocationScore(Integer allocationScore) { this.allocationScore = allocationScore; }
    public Integer getDurationScore() { return durationScore; }
    public void setDurationScore(Integer durationScore) { this.durationScore = durationScore; }
    public Integer getExperienceScore() { return experienceScore; }
    public void setExperienceScore(Integer experienceScore) { this.experienceScore = experienceScore; }
    public Integer getKnowledgeScore() { return knowledgeScore; }
    public void setKnowledgeScore(Integer knowledgeScore) { this.knowledgeScore = knowledgeScore; }
    public Integer getToleranceScore() { return toleranceScore; }
    public void setToleranceScore(Integer toleranceScore) { this.toleranceScore = toleranceScore; }
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}