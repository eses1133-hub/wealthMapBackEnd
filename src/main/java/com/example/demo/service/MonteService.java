package com.example.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.MonteResponseDTO;
import com.example.demo.dto.YearlyDataDTO;
import com.example.demo.entity.Investment;
import com.example.demo.entity.MonteCarloSimulation;
import com.example.demo.entity.User;
import com.example.demo.repository.MonteRepository;
import com.example.demo.repository.UserRepository;

import java.math.RoundingMode;
import java.util.*;

@Service
public class MonteService {
	@Autowired
    private MonteRepository monteCarloRepository; // 存模擬結果

    @Autowired
    private UserRepository userRepository; //找 User 物件

    public MonteResponseDTO calculateSimulation(
            Long userId, double monthly, int years, BigDecimal initialAmount,
            Map<String, Double> allocations, double inflationRate) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + userId + " 的使用者"));

        // 1. 計算資產配置的平均報酬與標準差
        double totalAvg = 0;
        double totalStd = 0;
     
        for (Map.Entry<String, Double> entry : allocations.entrySet()) {
            double[] data = ASSET_CONFIG.get(entry.getKey());
            
            if (data == null) {
                throw new RuntimeException("無效的資產類型：" + entry.getKey());
            }
            
            double ratio = entry.getValue();
            totalAvg += data[0] * ratio; 
            totalStd += data[1] * ratio; 
        }

        // 2. 初始化模擬參數
        Random random = new Random();
        double monthlyAvgReturn = totalAvg / 12;      
        double monthlyStdDev = totalStd / Math.sqrt(12);
        BigDecimal monthlyInflationDivisor = BigDecimal.valueOf(Math.pow(1 + inflationRate, 1.0/12.0));
        
        // 建立容器：1000次模擬，每年年底的金額
        double[][] allTrajectories = new double[1000][years + 1];
        BigDecimal[] yearlyCosts = new BigDecimal[years + 1];

        // 3. 開始 1000 次蒙地卡羅模擬
        List<BigDecimal> finalResultsList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            BigDecimal balance = initialAmount;
            allTrajectories[i][0] = balance.doubleValue();
            BigDecimal monthlyDeposit = BigDecimal.valueOf(monthly);

            for (int year = 1; year <= years; year++) {
                for (int month = 0; month < 12; month++) {
                    double randomReturn = monthlyAvgReturn + (random.nextGaussian() * monthlyStdDev);            
                    balance = balance.add(monthlyDeposit).multiply(BigDecimal.valueOf(1 + randomReturn));
                    balance = balance.divide(monthlyInflationDivisor, 4, RoundingMode.HALF_UP);
                }
                allTrajectories[i][year] = balance.doubleValue(); 
            }
            finalResultsList.add(balance);
        }

        // 4. 計算每年投入成本 (藍色階梯線資料)
        for (int y = 0; y <= years; y++) {
            yearlyCosts[y] = initialAmount.add(
                BigDecimal.valueOf(monthly).multiply(BigDecimal.valueOf(12)).multiply(BigDecimal.valueOf(y))
            );
        }

     // 5. 整理畫圖用的年度資料 (trajectories)
        List<YearlyDataDTO> trajectories = new ArrayList<>();
        for (int year = 0; year <= years; year++) {
            List<Double> yearData = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                yearData.add(allTrajectories[i][year]);
            }
            Collections.sort(yearData);

            YearlyDataDTO dto = new YearlyDataDTO();
            dto.setYear(year);

            if (year == 0) {
            	double startVal = initialAmount.doubleValue();
                dto.setVal(startVal);
                dto.setConfidenceLow(startVal);
                dto.setConfidenceHigh(startVal);
                dto.setCost(startVal);
            } else {
                dto.setVal(yearData.get(499));           // 中位數
                dto.setConfidenceLow(yearData.get(99));  // 10% 悲觀
                dto.setConfidenceHigh(yearData.get(899));// 90% 樂觀
                dto.setCost(yearlyCosts[year].doubleValue());
            }
            trajectories.add(dto);
        }

        // 6. 排序最終結果以取得百分位數
        Collections.sort(finalResultsList);
        BigDecimal pessimistic = finalResultsList.get(99);
        BigDecimal normal = finalResultsList.get(499);
        BigDecimal optimistic = finalResultsList.get(899);
        BigDecimal totalContributed = yearlyCosts[years];

        // 7. 存入資料庫 (Entity)
        MonteCarloSimulation entity = new MonteCarloSimulation();
        entity.setUser(user);
        entity.setMonthlyInvestment(monthly);
        entity.setInvestmentYears(years);
        entity.setAnnualReturn(totalAvg); 
        entity.setStandardDeviation(totalStd); 
        entity.setPessimistic(pessimistic.setScale(2, RoundingMode.HALF_UP));
        entity.setNormal(normal.setScale(2, RoundingMode.HALF_UP));
        entity.setOptimistic(optimistic.setScale(2, RoundingMode.HALF_UP));
        entity.setTotalInvestment(totalContributed.setScale(2, RoundingMode.HALF_UP));
        monteCarloRepository.save(entity);

        // 8. 組裝回傳前端的 DTO
        MonteResponseDTO response = new MonteResponseDTO();
        response.setPessimistic(pessimistic.doubleValue());
        response.setNormal(normal.doubleValue());
        response.setOptimistic(optimistic.doubleValue());
        response.setTotalInvestment(totalContributed.doubleValue());
        response.setTrajectories(trajectories);

        return response;
    }
    private static final Map<String, double[]> ASSET_CONFIG = Map.of(
            "STOCK", new double[]{0.08, 0.18},
            "BOND",  new double[]{0.02, 0.04},
            "GOLD",  new double[]{0.04, 0.15},
            "CASH",  new double[]{0.015, 0.005}
    );
}
