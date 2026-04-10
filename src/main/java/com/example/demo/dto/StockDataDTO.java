package com.example.demo.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/*
 * 抓取股價API的對應DTO
 */
@Data
public class StockDataDTO {
	private String date;
	@JsonProperty("stock_id")
	private String stockId;
	@JsonProperty("Trading_Volume")
	private long tradingVolume;
	@JsonProperty("Trading_money")
	private long tradingMoney;
	private double open;
	private double max;
	private double min;
	private double close;
	private double spread;
	@JsonProperty("Trading_turnover")
	private int tradingTurnover;

}
