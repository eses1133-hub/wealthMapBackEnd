package com.example.demo.constant;

import java.util.Map;

public enum RiskLevel {
    
//    CONSERVATIVE("保守型", 15, 75, 10), // 1 - 10 分
//    DEFENSIVE("穩健型", 35, 55, 10),    // 11 - 15 分
//    BALANCED("平衡型", 50, 35, 15),     // 16 - 20 分
//    GROWTH("積極型", 70, 15, 15),       // 21 - 25 分
//    AGGRESSIVE("衝刺型", 85, 5, 10);    // 26 - 30 分

	CONSERVATIVE("保守型", "您屬於保守型投資人，風險承受度低。建議以資本保全為首要目標。", 15, 15, 80, 5),  // 1 - 15 分
	DEFENSIVE("穩健型", "您屬於穩健型投資人，願意承擔適量風險以追求有潛力的報酬。", 30, 45, 45, 10),        // 16 - 30 分
	GROWTH("積極型", "您屬於積極型投資人，追求資本長線增值，能忍受市場較大的波動。", 50, 75, 15, 10);       // 31 - 50 分
	
    private final String description;
    private final String advice;
    private final int maxScore;     		// 該等級的分數上限
    private final int equityPercent;		// 權益型占比
    private final int bondPercent;			// 固定收益占比
    private final int altPercent;			// 另類投資占比
    
    //RiskLevel(風險類別,權益型資產 (股票/基金)佔比,固定收益 (債券/定存)佔比,另類投資 (房產/黃金)佔比)
//    RiskLevel(String description, int equityPercent, int bondPercent, int altPercent) {
	RiskLevel(String description, String advice, int maxScore, int equityPercent, int bondPercent, int altPercent) {
       
        this.description = description;
        this.advice = advice;
        this.maxScore = maxScore;
        this.equityPercent = equityPercent;
        this.bondPercent = bondPercent;
        this.altPercent = altPercent;
    }
	
	// 根據總分判定等級的靜態方法
    public static RiskLevel fromScore(int score) {
        if (score <= 15) return CONSERVATIVE;
        if (score <= 30) return DEFENSIVE;
        return GROWTH;
    }

    // ... (保留之前的 Getters)
    public String getDescription() { return description; }
    public int getEquityPercent() { return equityPercent; }
    public int getBondPercent() { return bondPercent; }
    public int getAltPercent() { return altPercent; }

    public String getAdvice() { return advice; }
    public int getMaxScore() { return maxScore; }
    
    public Map<String, Integer> getAllocationMap() {
        return Map.of(
            "權益型資產 (股票/基金)", equityPercent,
            "固定收益 (債券/定存)", bondPercent,
            "另類投資 (房產/黃金)", altPercent
        );
    }
}