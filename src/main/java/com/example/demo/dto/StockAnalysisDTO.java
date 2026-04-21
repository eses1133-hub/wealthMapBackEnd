package com.example.demo.dto;

import lombok.Data;

/*
 * 股票的相關計算
 */

@Data
public class StockAnalysisDTO {
	
	private String symbol;
    private double currentPrice;
    private double ma20;         // 20日平均線
    private double deviation;    // 乖離率 (與 MA20 的差距 %)
    private String recommendation; // 加碼 / 減碼 / 觀望

}
