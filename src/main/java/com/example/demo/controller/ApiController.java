package com.example.demo.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Asset;
import com.example.demo.entity.StockPrice;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.StockPriceRepository;
import com.example.demo.service.StockService;
@RestController
@RequestMapping("/api/strategy-api")
@CrossOrigin(origins = "http://localhost:4200") // 允許 Angular 存取
public class ApiController {
	
	//	股價測試
	@Autowired
	private StockService stockService;
	
	@Autowired
	private AssetRepository assetRepository;
	
	@Autowired
    private StockPriceRepository stockPriceRepository;
	
	@GetMapping("/test")
	public String stockApiTest() {
		try {
			stockService.executeFetch();
			return "系統連接成功！";
		} catch (Exception e) {
			e.printStackTrace();
			return "連接失敗: " + e.getMessage();
		}
	}
	
	// 寄送加減碼通知 by email
	@GetMapping("/send-email-notification")
	public String stockApiTest1() {
		try {
			stockService.executeFetch();
			return "系統連接成功！";
		} catch (Exception e) {
			e.printStackTrace();
			return "連接失敗: " + e.getMessage();
		}
	}
	
    
	// 取得單一股票最近 20 天價格 (供 Chart.js 畫圖)
	@GetMapping("/stock-history/{symbol}")
	public ResponseEntity<List<StockPrice>> getStockHistory(@PathVariable("symbol") String symbol) {
		List<StockPrice> history = stockPriceRepository.findTop20BySymbolOrderByDateDesc(symbol);
		// 注意：回傳前可以先用 Collections.reverse(history) 讓時間軸由舊到新，方便畫圖
		Collections.reverse(history);
		return ResponseEntity.ok(history);
	}

	

}
