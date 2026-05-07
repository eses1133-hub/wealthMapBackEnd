package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data // 讓 Lombok 幫忙處理那一大堆 Getter/Setter，保持版面乾淨
public class Asset {
    @JsonProperty("stockId") 
    private String symbol;
    
    private String name;
    private double currentPrice;
    
    private double sharesOwned; 
    
    private double targetPercentage;
    private String suggestion;
}