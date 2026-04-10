package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//資料庫
@Entity
@Table(name = "simulation_results")//MySQL 表名
@Data
@Getter
@Setter
public class MonteCarloSimulation {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//使用者ID
	
	@Column(name = "monthly_investment", nullable = false)
	private double monthlyInvestment;

	@Column(name = "annual_return", nullable = false)
	private double annualReturn;

	@Column(name = "standard_deviation") 
	private double standardDeviation;
	
	@Column(name = "investment_years", nullable = false)
	private int investmentYears;
    
    // 存入模擬出的三個結果
	//悲觀
	@Column(nullable = false)
    private BigDecimal pessimistic;
	//中等
	@Column(nullable = false)
    private BigDecimal normal;
	//樂觀
	@Column(nullable = false)
    private BigDecimal optimistic;
	//總投入
	@Column(nullable = false)
    private BigDecimal totalInvestment;
	
	
	@Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //設定存檔時間
    @PrePersist
    protected void prePersist() {
        this.createdAt = LocalDateTime.now();
    }     
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
