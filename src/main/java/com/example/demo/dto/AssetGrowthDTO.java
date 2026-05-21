package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetGrowthDTO {

    private String month;

    private double growthRate;

}