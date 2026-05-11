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
//	  delete by carly	
//    @Transactional
//    public RiskAssessment evaluateAndSave(RiskAssessment assessment) {
//        
//        // 1. 計算總分 (6題，每題1-5分，總分範圍 6 ~ 30)
//        int totalScore = assessment.getAgeScore() + 
//                         assessment.getAllocationScore() + 
//                         assessment.getDurationScore() + 
//                         assessment.getExperienceScore() + 
//                         assessment.getKnowledgeScore() + 
//                         assessment.getToleranceScore();
//
//        // 🌟 2. 調整後的靈敏門檻 (讓總分 30 分的人能測到 AGGRESSIVE)
//        String level = "";
//        if (totalScore <= 10) {
//            level = "CONSERVATIVE"; // 保守
//        } else if (totalScore <= 15) {
//            level = "DEFENSIVE";    // 穩健
//        } else if (totalScore <= 20) {
//            level = "BALANCED";     // 平衡
//        } else if (totalScore <= 25) {
//            level = "GROWTH";       // 積極
//        } else {
//            level = "AGGRESSIVE";   // 衝刺
//        }
//
//        assessment.setRiskLevel(level);
//
//        // 3. 儲存結果
//        return riskAssessmentRepository.save(assessment);
//    }
//    // ==========================================
//    // 下面是你原本就寫好的其他功能，我幫你原封不動保留！
//    // ==========================================
//    @Transactional
//    public StrategyResponse evaluateRisk_old(RiskAssessmentRequest request) {
//        // 1. 計算總分 
//        int totalScore = request.calculateTotalScore();
//
//        // 2. 根據總分判定風險屬性
//        RiskLevel userLevel = determineLevel(totalScore);
//
//        // 3. 儲存結果到資料庫 
//        if (request.userId() != null) {
//            User user = userRepository.findById(request.userId())
//                .orElseThrow(() -> new RuntimeException("資料庫找不到此使用者 ID: " + request.userId()));
//            
//            user.setRiskLevel(userLevel.name()); // 設定風險等級
//            userRepository.save(user);    // 寫入資料庫
//        }
//
//        Map<String, Integer> allocation = Map.of(
//            "權益型資產 (股票/基金)", userLevel.getEquityPercent(),
//            "固定收益 (債券/定存)", userLevel.getBondPercent(),
//            "另類投資 (房產/黃金)", userLevel.getAltPercent()
//        );
//
//        return new StrategyResponse(
//            userLevel,
////            userLevel, 
//            allocation,
//            "根據您的問卷總分 (" + totalScore + "分)，您的投資屬性為：" + userLevel.getDescription(),
//            false
//        );
//    }
    
	// fix visitor can fill by carly
	// 會員模式：計算並儲存
	@Transactional
	public StrategyResponse evaluateRisk(RiskAssessmentRequest request) {
		StrategyResponse response = calculateOnly(request);

		
		// 只有會員模式 userId > 0 且存在時才存入資料庫
        if (request.userId() != null && request.userId() > 0) {
            userRepository.findById(request.userId()).ifPresent(user -> {
            	user.setRiskLevel(response.userLevel().name());
                userRepository.save(user);
            });
            
            // 只有會員才需要把題目明細轉成 Entity 存起來
            RiskAssessment assessment = new RiskAssessment();
            assessment.setQOneScore(request.qOneScore()); 
            assessment.setQTwoScore(request.qTwoScore()); 
            assessment.setQThreeScore(request.qThreeScore()); 
            assessment.setQFourScore(request.qFourScore()); 
            assessment.setQFiveScore(request.qFiveScore()); 
            assessment.setQSixScore(request.qSixScore()); 
            assessment.setQSevenScore(request.qSevenScore());
            assessment.setQEightScore(request.qEightScore()); 
            assessment.setQNineScore(request.qNineScore()); 
            assessment.setQTenScore(request.qTenScore());  
            assessment.setRiskLevel(response.userLevel().name());  
            assessment.setTotalScore(request.calculateTotalScore());  
            assessment.setUser(userRepository.getReferenceById(request.userId()));
            riskAssessmentRepository.save(assessment);
        }

		return response;
	}

	// 訪客模式：純邏輯計算 (不加 @Transactional，不寫入 DB)
	public StrategyResponse calculateOnly(RiskAssessmentRequest request) {
		// 1. 計算總分
		int totalScore = request.calculateTotalScore();
		// 2. 從 Enum 取得等級與相關資訊
		RiskLevel level = RiskLevel.fromScore(totalScore);
//		RiskLevel level = determineLevel(totalScore);

		
		// 3. 打包回傳 (對應你前端 Chart.js 需要的結構)
        return new StrategyResponse(
    		level,               // "CONSERVATIVE", "DEFENSIVE", "GROWTH"
//            level,
            level.getAllocationMap(),    // 從 RiskLevel 拿配比 Map
            "根據您的問卷總分 (" + totalScore + "分)，" + level.getAdvice(),          // 從 RiskLevel 拿文案
            request.isRiskOverMatch()  // 警示判斷
        );
	}
    
   /*
    *  依據userID 找尋最新的風險評估分數
   */
	public StrategyResponse getRiskResult(Long userId, String currentRiskLevel) {
	    return riskAssessmentRepository.findFirstByUserIdAndRiskLevelOrderByCreatedAtDesc(userId, currentRiskLevel)
	        .map(assessment -> {
	            // 將 String 轉回 Enum 取得配置圖資訊
	            RiskLevel level = RiskLevel.valueOf(assessment.getRiskLevel());
	            
	            return new StrategyResponse(
	                level,
	                level.getAllocationMap(),
	                "根據您最新的評估紀錄 (" + assessment.getTotalScore() + "分)，" + level.getAdvice(),
	                false
	            );
	        })
	        .orElseThrow(() -> new RuntimeException("找不到吻合的測驗紀錄"));
	}
    
}