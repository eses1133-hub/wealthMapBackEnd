package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.FinancialHealthDTO;
import com.example.demo.entity.AssetRecord;
import com.example.demo.repository.AssetRecordRepository;

@Service
public class AssetService {
	@Autowired
	private AssetRecordRepository repository;
	

	
	public AssetRecord save(AssetRecord record) {
		return repository.save(record);
	}
	
	public double calculateNetWorth(AssetRecord record) {
		
		double totalAsset = record.getCash()
		+record.getInvestment()
		+record.getInsurance()
		+record.getFund()
		+record.getProperty();
		
		double totalDebt = record.getDebt();	
		
		return totalAsset;
	}
	
	public FinancialHealthDTO analyze(AssetRecord record) {
		
		double netWorth = calculateNetWorth(record);
		FinancialHealthDTO dto = new FinancialHealthDTO();
		dto.setNetWorth(netWorth);
		
		if(netWorth>1000000) {
			dto.setLevel("健康");
		}else if(netWorth > 0 ) {
			dto.setLevel("普通");
		}else {
			dto.setLevel("危險");
		}
		
		return dto;
	}
}
