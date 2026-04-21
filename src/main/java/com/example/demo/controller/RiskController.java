package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import com.example.demo.entity.RiskAssessment;
import com.example.demo.entity.User;
import com.example.demo.service.RiskAssessmentService;
// 🌟 新增引入 UserRepository
import com.example.demo.repository.UserRepository; 

@RestController
@RequestMapping("/api/risk")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class RiskController {

    @Autowired
    private RiskAssessmentService riskService;

    // 🌟 新增 UserRepository，用來更新會員資料
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateRisk(@RequestBody Map<String, Object> payload) {
        
        try {
            Long userId = ((Number) payload.get("userId")).longValue();

            RiskAssessment assessment = new RiskAssessment();
            assessment.setAgeScore(((Number) payload.get("ageScore")).intValue());
            assessment.setAllocationScore(((Number) payload.get("allocationScore")).intValue());
            assessment.setDurationScore(((Number) payload.get("durationScore")).intValue());
            assessment.setExperienceScore(((Number) payload.get("experienceScore")).intValue());
            assessment.setKnowledgeScore(((Number) payload.get("knowledgeScore")).intValue());
            assessment.setToleranceScore(((Number) payload.get("toleranceScore")).intValue());

            // 先去資料庫把這個真實的使用者找出來 (而不是只 new 一個空的 User)
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("找不到使用者"));
            
            assessment.setUser(user);

            //  存進風險評估歷史紀錄 (你原本的邏輯)
//            RiskAssessment savedResult = riskService.evaluateAndSave(assessment);
//
//            String level = savedResult.getRiskLevel();
//
//           
//            //  同步更新會員資料表！讓大腦記住他的屬性
//            
//            user.setRiskLevel(level);
//            userRepository.save(user);
//
//            
//            //  打包前端需要的 StrategyResponse 大禮包
//           
            Map<String, Object> response = new HashMap<>();
//            response.put("userLevel", level);
//            
//            boolean isOverMatch = (assessment.getAgeScore() <= 2 && assessment.getToleranceScore() >= 4);
//            response.put("isRiskOverMatch", isOverMatch);
//
//            String advice = "";
//            Map<String, Integer> allocation = new HashMap<>();
//
//            switch(level) {
//                case "CONSERVATIVE":
//                    advice = "您屬於保守型投資人，無法承受過大資金波動。建議以保本為首要目標，將大部分資金配置於低風險的定存與高評等債券。";
//                    allocation.put("現金與定存", 60);
//                    allocation.put("政府債券", 30);
//                    allocation.put("大型穩健股", 10);
//                    break;
//                case "DEFENSIVE":
//                    advice = "您屬於穩健型投資人，能在承擔微小風險的前提下追求穩定收益。建議以債券為主，搭配少部分股票。";
//                    allocation.put("現金與定存", 30);
//                    allocation.put("投資級債券", 50);
//                    allocation.put("大型股/ETF", 20);
//                    break;
//                case "BALANCED":
//                    advice = "您屬於平衡型投資人，願意承受適度風險以換取合理報酬。股債平衡是您最好的選擇。";
//                    allocation.put("現金與定存", 10);
//                    allocation.put("債券", 40);
//                    allocation.put("股票/ETF", 50);
//                    break;
//                case "GROWTH":
//                    advice = "您屬於積極型投資人，追求資本長線增值，能忍受市場較大的波動。建議拉高股票資產的比重。";
//                    allocation.put("現金", 10);
//                    allocation.put("債券", 20);
//                    allocation.put("股票/ETF", 70);
//                    break;
//                case "AGGRESSIVE":
//                    advice = "您屬於衝刺型投資人，追求最高報酬，對短線劇烈波動不以為意。可考慮高成長股或科技股等資產。";
//                    allocation.put("現金", 5);
//                    allocation.put("高收益債", 10);
//                    allocation.put("股票/高風險資產", 85);
//                    break;
//            }
//
//            response.put("advice", advice);
//            response.put("allocation", allocation);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("資料解析失敗：" + e.getMessage());
        }
    }
    
 // ==========================================
    // 🌟 5. 查詢使用者是否做過測驗 (大禮包升級版)
    // ==========================================
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRiskLevel(@PathVariable("userId") Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("找不到使用者"));
            
            String level = user.getRiskLevel();
            
            // 💡 如果沒做過測驗 (level 是 null 或空字串)
            if (level == null || level.isEmpty()) {
                return ResponseEntity.ok(Map.of("hasTested", false));
            }

            // 💡 如果做過測驗，就把結果、建議、圖表比例一次打包給前端！
            Map<String, Object> response = new HashMap<>();
            response.put("hasTested", true);
            response.put("userLevel", level);
            response.put("isRiskOverMatch", false); // 預設給 false 即可

            String advice = "";
            Map<String, Integer> allocation = new HashMap<>();

            switch(level) {
                case "CONSERVATIVE":
                    advice = "您屬於保守型投資人，無法承受過大資金波動。建議以保本為首要目標，將大部分資金配置於低風險的定存與高評等債券。";
                    allocation.put("現金與定存", 60);
                    allocation.put("政府債券", 30);
                    allocation.put("大型穩健股", 10);
                    break;
                case "DEFENSIVE":
                    advice = "您屬於穩健型投資人，能在承擔微小風險的前提下追求穩定收益。建議以債券為主，搭配少部分股票。";
                    allocation.put("現金與定存", 30);
                    allocation.put("投資級債券", 50);
                    allocation.put("大型股/ETF", 20);
                    break;
                case "BALANCED":
                    advice = "您屬於平衡型投資人，願意承受適度風險以換取合理報酬。股債平衡是您最好的選擇。";
                    allocation.put("現金與定存", 10);
                    allocation.put("債券", 40);
                    allocation.put("股票/ETF", 50);
                    break;
                case "GROWTH":
                    advice = "您屬於積極型投資人，追求資本長線增值，能忍受市場較大的波動。建議拉高股票資產的比重。";
                    allocation.put("現金", 10);
                    allocation.put("債券", 20);
                    allocation.put("股票/ETF", 70);
                    break;
                case "AGGRESSIVE":
                    advice = "您屬於衝刺型投資人，追求最高報酬，對短線劇烈波動不以為意。可考慮高成長股或科技股等資產。";
                    allocation.put("現金", 5);
                    allocation.put("高收益債", 10);
                    allocation.put("股票/高風險資產", 85);
                    break;
            }

            response.put("advice", advice);
            response.put("allocation", allocation);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("讀取失敗：" + e.getMessage());
        }
    }
   
    
    
}