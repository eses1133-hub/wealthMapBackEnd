package com.example.demo.service;

import com.example.demo.constant.RiskLevel;
import com.example.demo.dto.RiskAssessmentRequest;
import com.example.demo.dto.StrategyResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository; // 引入 Repository
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 引入交易管控

import java.util.Map;

@Service
public class RiskAssessmentService {

    private final UserRepository userRepository;

    
    public RiskAssessmentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    
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
            
            user.setRiskLevel(userLevel); // 設定風險等級
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