package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Debt;
import com.example.demo.repository.DebtRepository;
import com.example.demo.service.DebtService;

@RestController
@RequestMapping("/api/debts")
public class DebtController {

	@Autowired
	private DebtRepository debtRepository;
	
	@Autowired
	private DebtService debtService;
	
	@GetMapping("/test/saveDebt")
	public String testsaveDebt() {
		Debt debt = new Debt();
		debt.setUserId(2L);
		debt.setDebtName("星展");	
		debt.setType("creditcard");
		debt.setTotalAmount(120000.0);
		debt.setPaidAmount(30000.0);
		debt.setMonthlyPayment(3000.0);
		debt.setDueDay(2);
		debt.setNotifyEnabled(true);
		debt.setActive(true);
	
		debtRepository.save(debt);
		
		return "OK";
	}
	
	@GetMapping("/hello")
	public String hello() {
	    return "hello";
	}
	
}
