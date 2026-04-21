package com.rebalance;

import com.rebalance.model.Asset;
import com.rebalance.service.RebalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RebalanceController {

    @Autowired
    private RebalanceService rebalanceService;

    @PostMapping("/calculate")
    public List<Asset> calculate(@RequestBody Map<String, Object> payload) {
        ObjectMapper mapper = new ObjectMapper();
        
        // 1. 轉換 Portfolio 列表
        List<Asset> portfolio = mapper.convertValue(payload.get("portfolio"), 
            mapper.getTypeFactory().constructCollectionType(List.class, Asset.class));
        
        // 2. 轉換目標總價值
        double targetTotalValue = Double.parseDouble(payload.get("targetTotalValue").toString());

        // 3. 呼叫 Service 並回傳 (這裡現在應該不會有紅線了！)
        return rebalanceService.calculate(portfolio, targetTotalValue);
    }
}