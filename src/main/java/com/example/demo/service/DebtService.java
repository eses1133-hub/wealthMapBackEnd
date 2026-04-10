package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Debt;

@Service
public class DebtService {

	public double calculateRemainingMonths(Debt debt) {

		double remainingMonths = 0;

		if (debt.getMonthlyPayment() == null ||
			debt.getMonthlyPayment() <= 0 ||
			debt.getTotalAmount() == null ||
			debt.getTotalAmount() <= 0 ||
			debt.getPaidAmount() == null ||
			debt.getPaidAmount() < 0 ||
			debt.getPaidAmount() > debt.getTotalAmount()) {
			return 0;
		}
		return remainingMonths = Math.ceil((debt.getTotalAmount() - debt.getPaidAmount()) / debt.getMonthlyPayment());
	}
}
