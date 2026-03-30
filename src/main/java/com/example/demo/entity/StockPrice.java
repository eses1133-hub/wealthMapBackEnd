package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "StockPrice")
@Data
@NoArgsConstructor // 自動生成無參數建構子
@AllArgsConstructor // 自動生成全參數建構子
public class StockPrice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	//記錄股市收盤日期
	@Column(name = "date", nullable = false)
	private LocalDate date;

	//證券代碼不能空白，最長100字
	@Column(name = "symbol", nullable = false, length = 100)
	private String symbol;
	
	@Column(name = "close_price")
	private Double closePrice;   // 收盤價
	
	@Column(name = "high_price")
    private Double highPrice;    // 當日最高價 (用於方案 B-2)
	
	@Column(name = "bias")
    private Double bias;	// 最近20筆的均價所計算的乖離率 
	
}
