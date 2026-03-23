package com.example.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AssetParameter;
import com.example.demo.entity.Investment;
import com.example.demo.entity.MonteCarloSimulation;
import com.example.demo.entity.User;
import com.example.demo.repository.AssetParameterRepository;
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
    @Autowired
    private AssetParameterRepository assetRepo;
    
    public Map<String, BigDecimal> calculateSimulation(
    		Long userId,double monthly, int years,BigDecimal initialAmount,
    		Map<String, Double> allocations, double inflationRate) {
    	
    	User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + userId + " 的使用者"));
    	//---------------
    	double totalAvg = 0;
        double totalStd = 0;
        for (Map.Entry<String, Double> entry : allocations.entrySet()) {
            AssetParameter param = assetRepo.findByAssetName(entry.getKey())
                .orElseThrow(() -> new RuntimeException("資料庫找不到資產：" + entry.getKey()));
            
            double ratio = entry.getValue();
            totalAvg += param.getAvgReturn().doubleValue() * ratio;
            totalStd += param.getStdDev().doubleValue() * ratio;
        }
        //--------------- 	
    	List<BigDecimal> results = new ArrayList<>();
        Random random = new Random();
        
        double monthlyAvgReturn = totalAvg / 12;      
        double monthlyStdDev = totalStd / Math.sqrt(12);
        int totalMonths = years * 12;
        
        BigDecimal monthlyInflationDivisor = BigDecimal.valueOf(Math.pow(1 + inflationRate, 1.0/12.0));

        for (int i = 0; i < 1000; i++) {
        	BigDecimal balance = initialAmount;
        	BigDecimal monthlyDeposit = BigDecimal.valueOf(monthly);
        	
        	for (int month = 0; month < totalMonths; month++) {
	            //隨機報酬率
	            double randomReturn = monthlyAvgReturn + (random.nextGaussian() * monthlyStdDev);            
	            //(現有餘額 + 每月投入) * (1 + 隨機報酬)            
	            BigDecimal multiplier = BigDecimal.valueOf(1 + randomReturn);
	            
	            balance = balance.multiply(multiplier).add(monthlyDeposit);
	            //扣掉通膨
	            balance = balance.divide(monthlyInflationDivisor, 4, RoundingMode.HALF_UP);
            }
        	results.add(balance.setScale(4, RoundingMode.HALF_UP));     
        } 
        BigDecimal totalContributed = initialAmount.add(
        	    BigDecimal.valueOf(monthly)
        	    .multiply(BigDecimal.valueOf(12))
        	    .multiply(BigDecimal.valueOf(years))
        	);
              
        Collections.sort(results);
    
        Map<String, BigDecimal> finalResults = new HashMap<>();

        finalResults.put("pessimistic", results.get(99).setScale(2, RoundingMode.HALF_UP));
        finalResults.put("normal", results.get(499).setScale(2, RoundingMode.HALF_UP));
        finalResults.put("optimistic", results.get(899).setScale(2, RoundingMode.HALF_UP));
        finalResults.put("totalInvestment", totalContributed.setScale(2, RoundingMode.HALF_UP));

        //資料包進 Entity
        MonteCarloSimulation entity = new MonteCarloSimulation();
        entity.setUser(user);
        entity.setMonthlyInvestment(monthly);
        entity.setInvestmentYears(years);
        entity.setAnnualReturn(totalAvg); 
        entity.setStandardDeviation(totalStd); 
        entity.setPessimistic(finalResults.get("pessimistic"));
        entity.setNormal(finalResults.get("normal"));
        entity.setOptimistic(finalResults.get("optimistic"));
        entity.setTotalInvestment(totalContributed);

        //存檔
        monteCarloRepository.save(entity);
        // --------------------------
        //印看看-------------------------------------------------------------------------
        System.out.println("--- 蒙地卡羅模擬結果 (1000次) ---");
        System.out.println("投資年限: " + years + " 年");
        System.out.println("悲觀情境 (10th Percentile): " + finalResults.get("pessimistic"));
        System.out.println("常態情境 (50th Percentile): " + finalResults.get("normal"));
        System.out.println("樂觀情境 (90th Percentile): " + finalResults.get("optimistic"));
        System.out.println("--------------------------------");
        //----------------------------------------------------------------------------------
        return finalResults;
        
    }
}
