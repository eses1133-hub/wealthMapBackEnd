package com.rebalance.service;

import java.util.List;
import com.rebalance.model.Asset;

public class RebalanceService {
    
    public void calculate(List<Asset> portfolio, double[] targetRatios) {
        // 修正點：加上 () 呼叫方法
        double totalValue = portfolio.stream().mapToDouble(Asset::getMarketValue).sum();
        
        System.out.println("========== 資產再平衡試算分析 ==========");
        System.out.printf("當前資產總價值： %.0f 元\n", totalValue);
        System.out.println("---------------------------------------");

        for (int i = 0; i < portfolio.size(); i++) {
            Asset a = portfolio.get(i);
            double currentRatio = (a.getMarketValue() / totalValue) * 100;
            double targetValue = totalValue * targetRatios[i];
            
            // 修正點：使用 a.getMarketValue()
            double diff = targetValue - a.getMarketValue();
            
            // 修正點：使用 a.getCurrentPrice()
            int adjustLots = (int) (diff / (a.getCurrentPrice() * 1000));

            System.out.printf("[%s]\n", a.getName());
            System.out.printf("  目前佔比: %.2f%% | 目標佔比: %.0f%%\n", currentRatio, targetRatios[i] * 100);
            
            if (adjustLots > 0) {
                System.out.println("  👉 建議操作: 【買進】 " + adjustLots + " 張");
            } else if (adjustLots < 0) {
                System.out.println("  👉 建議操作: 【賣出】 " + Math.abs(adjustLots) + " 張");
            } else {
                System.out.println("  👉 建議操作: 維持現狀 (差異不足一張)");
            }
        }
        System.out.println("=======================================");
    }
}