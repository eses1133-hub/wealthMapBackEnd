package com.example.demo.dto;

/**
 * 再平衡功能專用 DTO
 * 確保只傳送前端需要的欄位，不暴露 Entity 內部結構
 */
public record RebalanceResponseDTO(
    Long id,
    String symbol,
    Double targetPercentage,
    Integer currentShares,
    Long userId,
    boolean isActive
) {
}