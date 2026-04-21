package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.StrategyDTO;
import com.example.demo.entity.StockPrice;
import com.example.demo.repository.StockPriceRepository;

//負責「計算」。它只管從資料庫拿數字，然後套公式算乖離率。它不需要知道資料是從哪個 API 來的。
@Service
public class StrategyService {
	@Autowired
    private StockPriceRepository stockPriceRepository;
	
	/**
     * 計算策略訊號
     * @param symbol 股票代號
     * @param buyThreshold 使用者設定的加碼(買)門檻 (正數，如 5.0 代表 5%)
     * @param sellThreshold 使用者設定的減碼(賣)門檻 (正數，如 10.0 代表 10%)
     */
	public StrategyDTO calculateMa20Strategy(String symbol, double buyThreshold, double sellThreshold) {
		// 1. 從 DB 撈取最近 20 筆收盤價
		List<StockPrice> history20 = stockPriceRepository.findTop20BySymbolOrderByDateDesc(symbol);

		if (history20.size() < 20) {
			return null; // 資料不足 20 筆，無法計算 MA20
		}

		// 2. 計算 MA20 平均值
		double sum = history20.stream().mapToDouble(StockPrice::getClosePrice).sum();
		double ma20 = sum / history20.size();

		// 3. 取得最新一筆價格 (List 的第 0 筆)
		double currentPrice = history20.get(0).getClosePrice();

		LocalDate date = history20.get(0).getDate();

		// 4. 計算偏離率 (Bias)
		double bias = (currentPrice - ma20) / ma20;

		// 5. 判斷邏輯 (將輸入的 % 數轉為小數進行比對)
		String action = "觀望";
		boolean shouldNotify = false;

		if (bias <= -(buyThreshold / 100)) {
			action = "加碼";
			shouldNotify = true;
		} else if (bias >= (sellThreshold / 100)) {
			action = "減碼";
			shouldNotify = true;
		}

		return StrategyDTO.builder()
				.symbol(symbol)
				.currentPrice(currentPrice)
				.ma20(ma20).bias(bias)
				.action(action)
				.shouldNotify(shouldNotify)
				.date(date)
				.build();
	}

}
