package com.example.demo.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class HealthResponseDTO {

	@JsonProperty("L")
	private double liquidity;

	@JsonProperty("DTI")
	private double debtRatio;

	@JsonProperty("S")
	private double savingRate;
	private double score; // 總分
	private boolean hasAsset;
	private boolean hasLiability;
	private double totalAssets;
	private double totalLiabilities;
	private List<String> advice;
	private Map<String, Double> assetDistribution;
	private Map<String, Double> liabilityDistribution;

	public HealthResponseDTO(double liquidity, double debtRatio, double savingRate, double score, boolean hasAsset,
			boolean hasLiability, double totalAssets, double totalLiabilities) {
		this.liquidity = liquidity;
		this.debtRatio = debtRatio;
		this.savingRate = savingRate;
		this.score = score;
		this.hasAsset = hasAsset;
		this.hasLiability = hasLiability;
		this.totalAssets = totalAssets;
		this.totalLiabilities = totalLiabilities;
	}

}
