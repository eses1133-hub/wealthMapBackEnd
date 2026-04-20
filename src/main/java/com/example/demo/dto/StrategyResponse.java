package com.example.demo.dto;

import com.example.demo.constant.RiskLevel;
import java.util.Map;

public record StrategyResponse(
	    RiskLevel userLevel,
	    RiskLevel baselineLevel,
	    Map<String, Integer> allocation, // 注意這裡要是 Integer，對應你的 Map 內容
	    String advice,
	    boolean isRiskOverMatch
	) {}