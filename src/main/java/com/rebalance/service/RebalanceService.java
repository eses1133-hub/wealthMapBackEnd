package com.rebalance.service;

import com.rebalance.model.Asset;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RebalanceService {

    // 儲存所有股票的清單
    private List<Map<String, String>> allStocks = new ArrayList<>();

    @PostConstruct
    public void init() {
        // 請確保檔名與妳 resources 資料夾下的一模一樣
        String fileName = "/STOCK_DAY_ALL_20260326.csv"; 
        
        System.out.println("🔍 正在嘗試從 Classpath 載入檔案: " + fileName);

        try (InputStream is = getClass().getResourceAsStream(fileName)) {
            if (is == null) {
                System.err.println("❌ 錯誤：在 resources 找不到檔案 " + fileName);
                return;
            }

            // 證交所原始檔通常是 Big5 (MS950) 編碼
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                String line;
                br.readLine(); // 跳過標題列

                while ((line = br.readLine()) != null) {
                    line = line.replace("\"", ""); // 去除引號
                    String[] values = line.split(",");
                    
                    if (values.length > 2) {
                        Map<String, String> stock = new HashMap<>();
                        // 原始檔 index 1 是代號, index 2 是名稱
                        stock.put("symbol", values[1].trim()); 
                        stock.put("name", values[2].trim());
                        allStocks.add(stock);
                    }
                }
                System.out.println("✅ 載入成功！共 " + allStocks.size() + " 筆股票。");
            }
        } catch (Exception e) {
            System.err.println("❌ 讀取 CSV 發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 提供搜尋功能
    public List<Map<String, String>> searchStocks(String query) {
        return allStocks.stream()
                .filter(s -> s.get("symbol").contains(query) || s.get("name").contains(query))
                .limit(10)
                .collect(Collectors.toList());
    }

    // 計算試算邏輯
    public List<Asset> calculate(List<Asset> portfolio, double targetTotalValue) {
        for (Asset asset : portfolio) {
            double targetValue = targetTotalValue * asset.getTargetPercentage();
            double currentValue = asset.getCurrentPrice() * asset.getSharesOwned();
            double diff = targetValue - currentValue;
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