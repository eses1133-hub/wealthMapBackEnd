package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/*
 * 抓取台股名稱列表API的對應DTO
 */
@Data
public class StockIdNameDTO {
	@JsonProperty("stock_id")
    private String stockId;

    @JsonProperty("stock_name")
    private String stockName;

    @JsonProperty("industry_category")
    private String industryCategory;
}
