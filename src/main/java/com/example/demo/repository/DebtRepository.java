package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Debt;

public interface DebtRepository extends JpaRepository<Debt, Long>{
	List<Debt>findByDueDayAndNotifyEnabledAndActive(
		Integer dueDay,
		Boolean notifyEnabled,
		Boolean active
		);
}
