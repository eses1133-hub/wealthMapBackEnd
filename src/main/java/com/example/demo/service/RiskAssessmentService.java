package com.example.demo.service;

import com.example.demo.constant.RiskLevel;
import com.example.demo.dto.RiskAssessmentRequest;
import com.example.demo.dto.StrategyResponse;
import com.example.demo.entity.RiskAssessment; // 🌟 記得匯入實體
import com.example.demo.entity.User;
import com.example.demo.repository.RiskAssessmentRepository; // 🌟 記得匯入 Repository
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class RiskAssessmentService {

    private final UserRepository userRepository;
    
    // 🌟 1. 新增負責存「測驗紀錄」的 Repository
    private final RiskAssessmentRepository riskAssessmentRepository; 

    // 🌟 2. 把新的 Repository 加進建構子裡面
    public RiskAssessmentService(UserRepository userRepository, RiskAssessmentRepository riskAssessmentRepository) {
        this.userRepository = userRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
    }

    @Transactional
    public RiskAssessment evaluateAndSave(RiskAssessment assessment) {
        
        // 1. 計算總分 (把前端傳來的 6 題分數加總)
        int totalScore = assessment.getAgeScore() + 
                         assessment.getAllocationScore() + 
                         assessment.getDurationScore() + 
                         assessment.getExperienceScore() + 
                         assessment.getKnowledgeScore() + 
                         assessment.getToleranceScore();

        // 2. 根據總分判斷風險屬性 (分數區間你可以依照你們的企劃調整)
        String level = "";
        if (totalScore <= 12) {
            level = "CONSERVATIVE"; // 保守型
        } else if (totalScore <= 22) {
            level = "DEFENSIVE";    // 穩健型
        } else if (totalScore <= 32) {
            level = "BALANCED";     // 平衡型
        } else if (totalScore <= 42) {
            level = "GROWTH";       // 積極型
        } else {
            level = "AGGRESSIVE";   // 衝刺型
        }

        // 3. 把算出來的等級塞回問卷結果中
        assessment.setRiskLevel(level);

        // 4. 呼叫 Repository 幫忙存進資料庫，並回傳存好的整包資料
        return riskAssessmentRepository.save(assessment);
    }

    // ==========================================
    // 下面是你原本就寫好的其他功能，我幫你原封不動保留！
    // ==========================================
    @Transactional
    public StrategyResponse evaluateRisk(RiskAssessmentRequest request) {
        // 1. 計算總分 
        int totalScore = request.calculateTotalScore();

        // 2. 根據總分判定風險屬性
        RiskLevel userLevel = determineLevel(totalScore);

        // 3. 儲存結果到資料庫 
        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("資料庫找不到此使用者 ID: " + request.userId()));
            
            user.setRiskLevel(userLevel.name()); // 設定風險等級
            userRepository.save(user);    // 寫入資料庫
        }

        Map<String, Integer> allocation = Map.of(
            "權益型資產 (股票/基金)", userLevel.getEquityPercent(),
            "固定收益 (債券/定存)", userLevel.getBondPercent(),
            "另類投資 (房產/黃金)", userLevel.getAltPercent()
        );

        return new StrategyResponse(
            userLevel,
            userLevel, 
            allocation,
            "根據您的問卷總分 (" + totalScore + "分)，您的投資屬性為：" + userLevel.getDescription(),
            false
        );
    }
    
    private RiskLevel determineLevel(int score) {
        if (score <= 10) return RiskLevel.CONSERVATIVE;
        if (score <= 15) return RiskLevel.DEFENSIVE;
        if (score <= 20) return RiskLevel.BALANCED;
        if (score <= 25) return RiskLevel.GROWTH;
        return RiskLevel.AGGRESSIVE;
    }
}