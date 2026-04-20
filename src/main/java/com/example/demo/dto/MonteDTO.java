package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data; 

@Data
public class MonteDTO {
    private BigDecimal initialAmount;
    private double monthly;
    private int years;
    private double inflationRate; 
    private Map<String, Double> allocations;
}