package com.example.demo.dto;

import java.util.List;
import lombok.Data;

@Data
public class MonteResponseDTO {
    private double optimistic;
    private double normal;
    private double pessimistic;
    private double totalInvestment;
    
    private List<YearlyDataDTO> trajectories; 
}