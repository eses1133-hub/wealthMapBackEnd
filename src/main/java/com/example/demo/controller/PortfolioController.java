package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.constant.RiskLevel;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PortfolioController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/recommend/{userId}")
    public ResponseEntity<?> getRecommendedPortfolio(@PathVariable("userId") Long userId) {
        try {
            // 1. 去資料庫找這個人，並拿出他的風險屬性
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("找不到使用者"));
            
            RiskLevel level = user.getRiskLevel();
            if (level == null ) {
                return ResponseEntity.badRequest().body("該使用者尚未進行風險評估");
            }

            // 2. 準備推薦清單
            List<Map<String, String>> recommendations = new ArrayList<>();

            // 3. 根據屬性給予不同的推薦標的 (你可以自由修改這些標的)
            switch (level.name()) {
                case "CONSERVATIVE": // 保守型
                    recommendations.add(Map.of("name", "美國短期公債 ETF", "symbol", "SHV", "type", "債券", "description", "極低風險，適合資金停泊"));
                    recommendations.add(Map.of("name", "綜合債券 ETF", "symbol", "BND", "type", "債券", "description", "穩定配息，波動小"));
                    break;
                case "DEFENSIVE": // 穩健型
                    recommendations.add(Map.of("name", "全球投資級公司債", "symbol", "LQD", "type", "債券", "description", "收益率優於公債，風險可控"));
                    recommendations.add(Map.of("name", "高股息 ETF", "symbol", "VYM", "type", "股票", "description", "挑選高配息大型股，相對抗跌"));
                    break;
                case "BALANCED": // 平衡型
                    recommendations.add(Map.of("name", "標普500指數 ETF", "symbol", "VOO", "type", "股票", "description", "追蹤美國前500大企業，長期穩健"));
                    recommendations.add(Map.of("name", "全球總體債券 ETF", "symbol", "BNDW", "type", "債券", "description", "分散單一國家風險"));
                    break;
                case "GROWTH": // 積極型
                    recommendations.add(Map.of("name", "納斯達克100 ETF", "symbol", "QQQ", "type", "股票", "description", "聚焦科技巨頭，成長動能強"));
                    recommendations.add(Map.of("name", "全美股市 ETF", "symbol", "VTI", "type", "股票", "description", "包辦美國大中小企業，捕捉全面成長"));
                    break;
                case "AGGRESSIVE": // 衝刺型 (你截圖的等級)
                    recommendations.add(Map.of("name", "資訊科技板塊 ETF", "symbol", "VGT", "type", "股票", "description", "高波動、高報酬，專注尖端科技"));
                    recommendations.add(Map.of("name", "半導體 ETF", "symbol", "SOXX", "type", "股票", "description", "掌握 AI 與晶片產業爆發力"));
                    break;
                default:
                    return ResponseEntity.badRequest().body("未知的風險屬性");
            }

            // 4. 打包回傳給前端
            Map<String, Object> response = new HashMap<>();
            response.put("riskLevel", level);
            response.put("recommendations", recommendations);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("獲取推薦組合失敗：" + e.getMessage());
        }
    }
}