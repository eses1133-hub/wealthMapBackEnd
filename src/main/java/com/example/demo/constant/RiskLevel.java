package com.example.demo.constant;

public enum RiskLevel {
    
    CONSERVATIVE("保守型", 15, 75, 10), // 1 - 10 分
    DEFENSIVE("穩健型", 35, 55, 10),    // 11 - 15 分
    BALANCED("平衡型", 50, 35, 15),     // 16 - 20 分
    GROWTH("積極型", 70, 15, 15),       // 21 - 25 分
    AGGRESSIVE("衝刺型", 85, 5, 10);    // 26 - 30 分

    private final String description;
    private final int equityPercent;
    private final int bondPercent;
    private final int altPercent;

    RiskLevel(String description, int equityPercent, int bondPercent, int altPercent) {
        this.description = description;
        this.equityPercent = equityPercent;
        this.bondPercent = bondPercent;
        this.altPercent = altPercent;
    }

    // ... (保留之前的 Getters)
    public String getDescription() { return description; }
    public int getEquityPercent() { return equityPercent; }
    public int getBondPercent() { return bondPercent; }
    public int getAltPercent() { return altPercent; }
}