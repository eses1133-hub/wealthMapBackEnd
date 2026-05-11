package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "debts")
@Getter
@Setter
@Data
public class Debt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name = "debt_name",nullable = false,length = 100)
	private String debtName;
	
	@Column(nullable = false,length = 100)
	private String type;
	
	//總負債金額
	@Column(nullable = false)
	private Double totalAmount;
	
	//已還款金額
	@Column(nullable = false)
	private Double paidAmount;
	
	//每月預計還款
	@Column(nullable = false)
	private Double monthlyPayment;
	
	//每月繳款日
	@Column(nullable = false)
	private Integer dueDay;
	
	//是否啟用提醒
	@Column(nullable = false,length = 100)
	private Boolean notifyEnabled;
	
	//是否啟用中
	@Column(nullable = false,length = 100)
	private Boolean active;
	
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	
	private String email;
	
	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

}
