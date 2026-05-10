package com.example.demo.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class HealthResponseDTO {

	private double L; // L
	private double DTI; // DTI
	private double S; // S
	private double score; // 總分
	private boolean hasAsset;
	private boolean hasLiability;
	private double totalAssets;
	private double totalLiabilities;
	private List<String> advice;
	private Map<String, Double> assetDistribution;
	private Map<String, Double> liabilityDistribution;
	

	public HealthResponseDTO(double L, double DTI, double S, double score, boolean hasAsset, boolean hasLiability,double totalAssets,double totalLiabilities) {
		this.L = L;
		this.DTI = DTI;
		this.S = S;
		this.score = score;
		this.hasAsset = hasAsset;
		this.hasLiability = hasLiability;
		this.totalAssets = totalAssets;
		this.totalLiabilities = totalLiabilities;
	}



}
