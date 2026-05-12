package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.AssetGrowthDTO;
import com.example.demo.dto.HealthResponseDTO;
import com.example.demo.entity.Asset;
import com.example.demo.entity.Liability;
import com.example.demo.repository.AssetRecordRepository;
import com.example.demo.repository.LiabilityRepository;

@Service
public class HealthService {

	@Autowired
	private AssetRecordRepository assetRecordRepository;

	@Autowired
	private LiabilityRepository liabilityRepository;

	public HealthResponseDTO calculate(Long userId) {

		// 撈資產
		List<Asset> assets = Optional.ofNullable(assetRecordRepository.findByUser_Id(userId)).orElse(new ArrayList<>());

		// 撈負債
		List<Liability> liabilities = Optional.ofNullable(liabilityRepository.findByUser_Id(userId))
				.orElse(new ArrayList<>());

		boolean hasAsset = !assets.isEmpty();

		boolean hasLiability = !liabilities.isEmpty();
		if (!hasAsset && !hasLiability) {
			return new HealthResponseDTO(0, // L
					0, // DTI
					0, // S
					0, // score
					false, false, 0, 0);
		}

		// ===== 資產 =====
		double totalAssets = assets.stream().mapToDouble(a -> Optional.ofNullable(a.getAmount()).orElse(0.0)).sum();

		// ===== 負債 =====
		double totalLiabilities = liabilities.stream().mapToDouble(d -> Optional.ofNullable(d.getAmount()).orElse(0.0))
				.sum();

		double netWorth = totalAssets - totalLiabilities;

		// ===== 現金（流動資產）=====
		double cash = assets.stream().mapToDouble(d -> Optional.ofNullable(d.getAmount()).orElse(0.0)).sum();

		// ===== 每月支出 =====
		double expense = assets.stream().filter(a -> "EXPENSE".equals(a.getType()))
				.mapToDouble(a -> Optional.ofNullable(a.getAmount()).orElse(0.0)).sum();

		double income = assets.stream().filter(a -> "INCOME".equals(a.getType()))
				.mapToDouble(a -> Optional.ofNullable(a.getAmount()).orElse(0.0)).sum();

		// ===== 財務指標 =====
		double L = expense > 0 ? Math.min(netWorth / expense, 6) : 0;

		double DTI = totalLiabilities > 0 ? Math.min(((totalLiabilities / 36) / income) * 100, 100) : 0;

		double S = expense > 0 ? ((income - expense) / income) * 100 : 0;

		// ===== 分數 =====
		double score = Math.min(calculateScore(L, DTI, S), 100);

		// ===== 等級 =====
		String level = calculateLevel(score);

		// ===== 分類（給圖表用🔥）=====
		Map<String, Double> assetDistribution = assets.stream().collect(Collectors.groupingBy(Asset::getType,
				Collectors.summingDouble(a -> Optional.ofNullable(a.getAmount()).orElse(0.0))));

		Map<String, Double> liabilityDistribution = liabilities.stream().collect(Collectors.groupingBy(
				Liability::getCategory, Collectors.summingDouble(d -> Optional.ofNullable(d.getAmount()).orElse(0.0))));

		// ===== 建議系統 =====
		List<String> advice = generateAdvice(L, DTI, S, netWorth);

		// ===== 回傳 =====
		HealthResponseDTO dto = new HealthResponseDTO(L, DTI, S, score, hasAsset, hasLiability, totalAssets,
				totalLiabilities);
		// 🔥 補上你原本算但沒回傳的資料
		dto.setAssetDistribution(assetDistribution);
		dto.setLiabilityDistribution(liabilityDistribution);
		dto.setAdvice(advice);
//		System.out.println("緊急預備金=" + L);
//		System.out.println("負債比=" + DTI);
//		System.out.println("儲蓄率=" + S);
//		System.out.println("分數=" + score);
//		System.out.println("收入=" + income);
//		System.out.println("支出=" + expense);
		return dto;
	}

	public List<AssetGrowthDTO> getAssetGrowth(Long userId) {

		List<Object[]> rawData = assetRecordRepository.getMonthlyAssets(userId);

		List<AssetGrowthDTO> result = new ArrayList<>();

		double previous = 0;

		for (Object[] row : rawData) {

			String month = (String) row[0];

			double total = ((Number) row[1]).doubleValue();

			double growth = 0;

			if (previous > 0) {

				growth = ((total - previous) / previous) * 100;

			}

			result.add(new AssetGrowthDTO(month, Math.round(growth * 10.0) / 10.0));

			previous = total;
		}

		return result;
	}

	private double calculateScore(double L, double DTI, double S) {

		// 👉 簡單權重（可調整🔥）
		double lScore = Math.min(L, 6) * 10; // 上限60
//		double dtiScore = Math.max(0, 100 - DTI); // 越低越好
		double dtiScore;

		if (DTI <= 20) {
			dtiScore = 100;
		} else if (DTI <= 40) {
			dtiScore = 80;
		} else if (DTI <= 60) {
			dtiScore = 50;
		} else {
			dtiScore = 20;
		}
		double sScore = Math.max(0, S); // 越高越好

		return (lScore * 0.35) + (dtiScore * 0.3) + (sScore * 0.35);
	}

	private String calculateLevel(double score) {
		if (score >= 90)
			return "健康";
		if (score >= 70)
			return "穩定";
		return "危險";
	}

	private List<String> generateAdvice(double L, double DTI, double S, double netWorth) {

		List<String> advice = new ArrayList<>();

		if (L >= 12)
			advice.add("預備金極為充裕，可考慮更積極的資產配置。");
		else if (L >= 6)
			advice.add("預備金充足，可支撐長期投資佈局。");
		else if (L >= 3)
			advice.add("預備金尚可，足以應付短期突發狀況。");
		else {
			advice.add("預備金嚴重不足，應暫緩投資並優先儲蓄。");
		}

		if (DTI <= 15)
			advice.add("槓桿比例健康，具備良好抗風險空間。");
		else if (DTI <= 30)
			advice.add("負債尚在可控範圍，建議檢視非必要支出。");
		else if (DTI <= 45)
			advice.add("負債尚在可控範圍，建議檢視非必要支出。");
		else {
			advice.add("負債壓力沉重，需立即優化債務結構。");
		}

		if (S >= 60)
			advice.add("儲蓄力強勁，現金流充裕。");
		else if (S >= 40)
			advice.add("儲蓄表現優異，資本累積速度理想。");
		else if (S >= 20)
			advice.add("儲蓄水平正常，建議維持固定撥存習慣。");
		else {
			advice.add("儲蓄率偏低，建議強制執行儲蓄計畫。");
		}

		if (netWorth < 0) {
			advice.add("淨資產為負，建議優先降低負債");
		}

		if (advice.isEmpty()) {
			advice.add("財務狀況良好，請持續保持 👍");
		}

		return advice;
	}

//		String debtStatus;
//		if (DTI <= 15)
//			debtStatus = "槓桿比例健康，具備良好抗風險空間。";
//		else if (DTI <= 30)
//			debtStatus = "負債尚在可控範圍，建議檢視非必要支出。";
//		else if (DTI <= 45)
//			debtStatus = "負債尚在可控範圍，建議檢視非必要支出。";
//		else {
//			debtStatus = "負債壓力沉重，需立即優化債務結構。";
//		}
//
//		String investStatus;
//		if (G == 0)
//			investStatus = "⚠️尚未設定財務目標，建議先建立投資計畫。";
//		else if (G >= 95)
//			investStatus = "即將達成財務自由，建議維持現有配置。";
//		else if (G >= 80)
//			investStatus = "接近財務自由目標，建議逐步增加資產配置。";
//		else if (G >= 60)
//			investStatus = "達成率穩定增長，建議持續投入。";
//		else {
//			investStatus = "達成率有待提升，建議重新檢視投資標的。";
//		}
//
//		String liquidStatus;
//		if (L >= 12)
//			liquidStatus = "預備金極為充裕，可考慮更積極的資產配置。";
//		else if (L >= 6)
//			liquidStatus = "預備金充足，可支撐長期投資佈局。";
//		else if (L >= 3)
//			liquidStatus = "預備金尚可，足以應付短期突發狀況。";
//		else {
//			liquidStatus = "預備金嚴重不足，應暫緩投資並優先儲蓄。";
//		}
//
//		String savingStatus;
//		if (S >= 60)
//			savingStatus = "儲蓄力強勁，現金流充裕。";
//		else if (S >= 40)
//			savingStatus = "儲蓄表現優異，資本累積速度理想。";
//		else if (S >= 20)
//			savingStatus = "儲蓄水平正常，建議維持固定撥存習慣。";
//		else {
//			savingStatus = "儲蓄率偏低，建議強制執行儲蓄計畫。";
//		}
//
//		// ===== 7️⃣ 分數 =====
//		int overallScore = (int) ((L * 10 + (100 - DTI) + S + G) / 4);
//
//		// ===== 8️⃣ 回傳 =====
//		HealthResponseDTO res = new HealthResponseDTO();
//		res.setL(L);
//		res.setDTI(DTI);
//		res.setS(S);
//		res.setG(G);
//		res.setScore(overallScore);
//		res.setDebtStatus(debtStatus);
//		res.setInvestStatus(investStatus);
//		res.setLiquidStatus(liquidStatus);
//		res.setSavingStatus(savingStatus);
//
//		return res;
//	}

//		//權重
//		double baseweight = hasGoal ? 100 / 3 / 1.2 : 100 / 3;
//
//		//各個指標的分數
//		double scoreL = Math.min(baseweight, (L / 6) * baseweight);
//		double scoreDTI = Math.min(baseweight, (Math.max(0, 50 - DTI) / 50) * baseweight);
//		double scoreS = Math.min(baseweight, (S / 20) * baseweight);
//		double scoreG = hasGoal ? Math.min(investweight, (G / 100) * investweight) : 0;
//		
//		//總分
//		int overallScore = (int) ((L * 10 + (100 - DTI) + S + G) / 4);
//
//		HealthResponseDTO res = new HealthResponseDTO();
//		res.setL(L);
//		res.setDTI(DTI);
//		res.setS(S);
//		res.setG(G);
//		res.setScore(overallScore);
//		res.setDebtStatus(debtStatus);
//		res.setInvestStatus(investStatus);
//		res.setLiquidStatus(liquidStatus);
//		res.setSavingStatus(savingStatus);
//		return res;
//	}
}