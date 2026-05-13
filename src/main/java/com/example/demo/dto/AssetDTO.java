package com.example.demo.dto;

public record AssetDTO(
		Long id,
	    String name,
	    String type,
	    Double amount,       // 現金類的總金額
	    String stockId,      // 股票代號
	    Double sharesOwned,  // 持有股數
	    Double cost     // 總成本) {

		) {}
