package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.HealthRequestDTO;
import com.example.demo.dto.HealthResponseDTO;

@Service
public class HealthService {

	public HealthResponseDTO calculate(HealthRequestDTO req) {

		double totalDebt = req.getMortgage() + req.getCarLoan() + req.getPersonalLoan() + req.getCreditCard();

		double L = req.getExpense() > 0 ? (req.getSavings() + req.getCash()) / (double) req.getExpense() : 0;

		double DTI = req.getIncome() > 0 ? (totalDebt / req.getIncome()) * 100 : 0;

		double S = req.getIncome() > 0 ? ((req.getIncome() - req.getExpense()) / (double) req.getIncome()) * 100 : 0;

//		boolean hasGoal = req.getG() != null && req.getG() > 0;
		
		boolean hasGoal = req.getG() > 0;
		double G = hasGoal ? req.getG() : 0;
		double investweight = 20;

		String debtStatus;
		if (DTI <= 15)
			debtStatus = "槓桿比例健康，具備良好抗風險空間。";
		else if (DTI <= 30)
			debtStatus = "負債尚在可控範圍，建議檢視非必要支出。";
		else if (DTI <= 45)
			debtStatus = "負債尚在可控範圍，建議檢視非必要支出。";
		else {
			debtStatus = "負債壓力沉重，需立即優化債務結構。";
		}

		String investStatus;
		if (G == 0)
			investStatus = "⚠️尚未設定財務目標，建議先建立投資計畫。";
		else if (G >= 95)
			investStatus = "即將達成財務自由，建議維持現有配置。";
		else if (G >= 80)
			investStatus = "接近財務自由目標，建議逐步增加資產配置。";
		else if (G >= 60)
			investStatus = "達成率穩定增長，建議持續投入。";
		else {
			investStatus = "達成率有待提升，建議重新檢視投資標的。";
		}

		String liquidStatus;
		if (L >= 12)
			liquidStatus = "預備金極為充裕，可考慮更積極的資產配置。";
		else if (L >= 6)
			liquidStatus = "預備金充足，可支撐長期投資佈局。";
		else if (L >= 3)
			liquidStatus = "預備金尚可，足以應付短期突發狀況。";
		else {
			liquidStatus = "預備金嚴重不足，應暫緩投資並優先儲蓄。";
		}

		String savingStatus;
		if (S >= 60)
			savingStatus = "儲蓄力強勁，現金流充裕。";
		else if (S >= 40)
			savingStatus = "儲蓄表現優異，資本累積速度理想。";
		else if (S >= 20)
			savingStatus = "儲蓄水平正常，建議維持固定撥存習慣。";
		else {
			savingStatus = "儲蓄率偏低，建議強制執行儲蓄計畫。";
		}

		//權重
		double baseweight = hasGoal ? 100 / 3 / 1.2 : 100 / 3;

		//各個指標的分數
		double scoreL = Math.min(baseweight, (L / 6) * baseweight);
		double scoreDTI = Math.min(baseweight, (Math.max(0, 50 - DTI) / 50) * baseweight);
		double scoreS = Math.min(baseweight, (S / 20) * baseweight);
		double scoreG = hasGoal ? Math.min(investweight, (G / 100) * investweight) : 0;
		
		//總分
		int overallScore = (int) ((L * 10 + (100 - DTI) + S + G) / 4);

		HealthResponseDTO res = new HealthResponseDTO();
		res.setL(L);
		res.setDTI(DTI);
		res.setS(S);
		res.setG(G);
		res.setScore(overallScore);
		res.setDebtStatus(debtStatus);
		res.setInvestStatus(investStatus);
		res.setLiquidStatus(liquidStatus);
		res.setSavingStatus(savingStatus);
		return res;
	}
}