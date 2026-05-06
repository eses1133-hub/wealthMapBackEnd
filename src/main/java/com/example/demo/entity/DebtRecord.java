package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DebtRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;

	private Long userId;
	// 負債
	@Column(name = "Mortgage", nullable = true, length = 100)
	private Double Mortgage;
	@Column(name = "carLoan", nullable = true, length = 100)
	private Double carLoan;
	@Column(name = "creditCardfee", nullable = true, length = 100)
	private Double creditCardfee;
	@Column(name = "Loan", nullable = true, length = 100)
	private Double Loan;
	@Column(name = "debt", nullable = true, length = 100)
	private Double debt;

	// 記錄這筆資產建立的時間
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
}
