package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

// @Builder 是 Lombok 套件提供的一個註解。
// 主要作用是**「流式接口 (Fluent Interface)」**的方式來創建物件，而不是用傳統又長又臭的建構子（Constructor）。

@Data
@Builder
public class StrategyDTO {
	private String symbol;      // 股票代號
    private double currentPrice; // 最新價格 (P_today)
    private double ma20;        // 20日均線 (Moving Average)
    private double bias;        // 乖離率 ( (Price - MA20) / MA20 )
    private String action;      // 建議動作： "加碼", "減碼", "觀望"
    private boolean shouldNotify; // 是否達到發送通知的門檻
}
