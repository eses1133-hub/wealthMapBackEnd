package com.example.demo.dto;

import lombok.Data;

@Data
public class HealthResponseDTO {

	private double L; // 流動性
	private double DTI; // 負債比
	private double S; // 儲蓄率
	private double G; // 目標達成率

	private int score; // 總分


	private String debtStatus; //負債比建議
	private String investStatus; //投資建議
	private String liquidStatus; //流動性建議
	private String savingStatus; //儲蓄率建議

}
