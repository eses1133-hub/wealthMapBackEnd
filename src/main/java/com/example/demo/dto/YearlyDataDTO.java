package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearlyDataDTO {
    private int year;    
    private double cost;             // 累積投入本金 (藍色階梯線)
    private double confidenceHigh;   // 較佳預期 
    private double val;              // 一般預期 
    private double confidenceLow;    // 較差預期
}