package com.example.demo.controller;

import com.example.demo.dto.RiskAssessmentRequest;
import com.example.demo.dto.StrategyResponse;
import com.example.demo.service.RiskAssessmentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risk")
@CrossOrigin(origins = "http://localhost:4200")
public class RiskAssessmentController {

    private final RiskAssessmentService riskService;

    public RiskAssessmentController(RiskAssessmentService riskService) {
        this.riskService = riskService;
    }

    @PostMapping("/evaluate1")
    public StrategyResponse evaluate(@RequestBody RiskAssessmentRequest request) {
        // 直接將前端傳來的 6 題分數交給 Service 處理
        return riskService.evaluateRisk(request);
    }
}