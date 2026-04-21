package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Asset;
import com.example.demo.service.RebalanceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
// @CrossOrigin(origins = "*") // 允許前端存取
public class RebalanceController {

    @Autowired
    private RebalanceService rebalanceService;

    // 新增：供前端搜尋股票名稱的 API
    @GetMapping("/search")
    public List<Map<String, String>> search(@RequestParam String q) {
        return rebalanceService.searchStocks(q);
    }

    // 原有：執行再平衡試算的 API
    @PostMapping("/calculate")
    public List<Asset> calculate(@RequestBody Map<String, Object> payload) {
    	// 1. 先獲取原始的 List (此時裡面是 Map)
    	List<?> rawList = (List<?>) payload.get("portfolio");

    	// 2. 使用 ObjectMapper 將 List<Map> 轉換為 List<Asset>
    	ObjectMapper mapper = new ObjectMapper();
    	List<Asset> portfolio = mapper.convertValue(
    	    rawList, 
    	    new TypeReference<List<Asset>>() {}
    	);
    	
    	
//        List<Asset> portfolio = (List<Asset>) payload.get("portfolio");
        double targetTotalValue = Double.parseDouble(payload.get("targetTotalValue").toString());
        return rebalanceService.calculate(portfolio, targetTotalValue);
    }
}