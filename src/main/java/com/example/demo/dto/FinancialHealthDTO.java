package com.example.demo.dto;

import lombok.Setter;

@Setter
public class FinancialHealthDTO {
	private double netWorth;
	private String level; //健康 / 普通 / 危險
}
