package com.example.demo.controller;

import com.example.demo.dto.Asset;
import com.example.demo.service.RebalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // 允許前端存取
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
        List<Asset> portfolio = (List<Asset>) payload.get("portfolio");
        double targetTotalValue = Double.parseDouble(payload.get("targetTotalValue").toString());
        return rebalanceService.calculate(portfolio, targetTotalValue);
    }
}