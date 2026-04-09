package com.example.demo.dto;

public record StrategyResponseDTO(
	Long id,
    String symbol,
    Double buyThreshold,
    Double sellThreshold,
    boolean isActive,
    Long userId // 只傳 ID，不傳物件
) {

}
