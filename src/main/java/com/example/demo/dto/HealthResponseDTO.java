package com.example.demo.dto;

import lombok.Data;

@Data
public class HealthResponseDTO {

	private double L;   //流動性
	private double DTI; //負債比
	private double S; //儲蓄率
	private double G; //投資成功率

	private int score;  //總分
	private String level;  //等級A/B/C
}
