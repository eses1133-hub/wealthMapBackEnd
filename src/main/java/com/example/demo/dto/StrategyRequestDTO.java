package com.example.demo.dto;

import com.example.demo.entity.StrategySetting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StrategyRequestDTO(
	Long id,
	@NotBlank(message = "股票代號不能為空")
	String symbol,

	@NotNull(message = "加碼門檻必填")
	Double buyThreshold,

	@NotNull(message = "減碼門檻必填")
	Double sellThreshold,
	
    boolean isActive
) {
	/**
     * 手動定義靜態工具方法：將資料庫實體轉為 DTO
     * 這樣 Controller 就能呼叫 StrategyRequestDTO.fromEntity(saved)
     */
    public static StrategyRequestDTO fromEntity(StrategySetting s) {
        return new StrategyRequestDTO(
            s.getId(),
            s.getSymbol(),
            s.getBuyThreshold(),
            s.getSellThreshold(),
            s.isActive()
        );
    }
	
}