package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Debt;
import com.example.demo.entity.DebtRecord;

public interface DebtRecordRepository extends JpaRepository<Debt, Long> {
	List<Debt> findByUserId(Long userId);
}
