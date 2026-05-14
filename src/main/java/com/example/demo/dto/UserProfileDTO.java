package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.dto.StrategyResponseDTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // 自動生成無參數建構子
@AllArgsConstructor // 自動生成全參數建構子
public class UserProfileDTO {
//	private String name;
//	private String email;

	// getter / setter
	
	//2026-04-08 by carly
	private long id;
	private String name;
    private String email;
    private String role;
    private String riskLevel;
    private boolean enabled;
    
    private List<AssetDTO> assets;
    private List<InvestmentDTO> investments;
    private List<FinancialGoalDTO> financialGoals;
    private List<StrategyResponseDTO> strategySettings;
    
    // 內部靜態 DTO，方便資料傳輸且避免 Infinite Recursion
    @Data 
    public static class AssetDTO {
        private Long id;
        private String name;
        private String type;
        private String symbol;
        private Double amount;
        private Double shares;
        private Double cost;
        
    }
    @Data 
    public static class InvestmentDTO {
        private Long id;
        private String symbol;
    	private String type;
    	private Double quantity;
    	private Double buyPrice;
    	private Double currentPrice;
    }
    @Data 
    public static class FinancialGoalDTO {
    	private Long id;
        private String goalName;
        private Double targetAmount;
        private Double currentAmount;
        private LocalDate targetDate;
    }
    
    
}
