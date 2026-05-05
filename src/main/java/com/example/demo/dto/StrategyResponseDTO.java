package com.example.demo.dto;


/*
 * 使用者設定的加減碼門檻
 */
public record StrategyResponseDTO(
	Long id,
    String symbol,
    Double buyThreshold,
    Double sellThreshold,
    boolean isActive,
    Long userId // 只傳 ID，不傳物件
) {

}
