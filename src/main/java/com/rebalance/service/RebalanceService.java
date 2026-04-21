package com.rebalance.service;

import com.rebalance.model.Asset;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RebalanceService {

    // 這裡的參數必須是 (List<Asset>, double)
    public List<Asset> calculate(List<Asset> portfolio, double targetTotalValue) {
        for (Asset asset : portfolio) {
            double targetValue = targetTotalValue * asset.getTargetPercentage();
            double currentValue = asset.getCurrentPrice() * asset.getSharesOwned();
            double diff = targetValue - currentValue;
            
            // 計算股數
            int sharesToChange = (int) (diff / asset.getCurrentPrice());

            if (sharesToChange > 0) {
                asset.setSuggestion("建議買入 " + sharesToChange + " 股");
            } else if (sharesToChange < 0) {
                asset.setSuggestion("建議賣出 " + Math.abs(sharesToChange) + " 股");
            } else {
                asset.setSuggestion("持股符合比例");
            }
        }
        return portfolio;
    }
}