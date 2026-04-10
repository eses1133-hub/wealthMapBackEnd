package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.entity.RiskAssessment;
import com.example.demo.repository.RiskAssessmentRepository;

@Service
public class RiskAssessmentService {

    @Autowired
    private RiskAssessmentRepository riskRepo;

    public RiskAssessment evaluateAndSave(RiskAssessment assessment) {
        int total = assessment.getAgeScore() + 
                    assessment.getAllocationScore() + 
                    assessment.getDurationScore() + 
                    assessment.getExperienceScore() + 
                    assessment.getKnowledgeScore() + 
                    assessment.getToleranceScore();
        
        assessment.setTotalScore(total);

        // 🌟 配合前端的翻譯機，改回存英文代號
        if (total <= 10) {
            assessment.setRiskLevel("CONSERVATIVE"); // 保守
        } else if (total <= 15) {
            assessment.setRiskLevel("DEFENSIVE");    // 穩健
        } else if (total <= 20) {
            assessment.setRiskLevel("BALANCED");     // 平衡
        } else if (total <= 25) {
            assessment.setRiskLevel("GROWTH");       // 積極
        } else {
            assessment.setRiskLevel("AGGRESSIVE");   // 衝刺
        }

        return riskRepo.save(assessment);
    }
}