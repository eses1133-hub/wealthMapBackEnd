package com.example.demo.dto;

import lombok.Data;

@Data
public class HealthRequestDTO {
	
	private int income;
    private int expense;
    
    private int savings;
    private int cash;
    
    private int mortgage;
    private int carLoan;
    private int personalLoan;
    private int creditCard;
    
    private int investmentSuccessRate;
}
