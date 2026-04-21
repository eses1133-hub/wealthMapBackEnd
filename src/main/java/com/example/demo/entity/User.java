package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import com.example.demo.constant.RiskLevel; // 引入我們寫好的 Enum

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;   // 新增 Enum 型別定義
import jakarta.persistence.Enumerated; // 新增 Enum 註解
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
	
    @Id	
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //使用者ID

    @Column(nullable = false, length = 100)
    private String name; //使用者名稱

    @Column(nullable = false, unique = true, length = 100)
    private String email; //使用者EMAIL

    @Column(nullable = false, length = 255)
    private String password; //使用者密碼
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
	private String role; // 使用者角色 (例如: "USER", "ADMIN")

    // ==========================================
    // 架構師新增：使用者的風險屬性 (存入資料庫為字串，如 "GROWTH")
    // ==========================================
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;
    
    //一個user可擁有很多資產，cascade = CascadeType.ALL=級聯操作
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)

    @BatchSize(size = 10)
    private List<Asset> assets = new ArrayList<>();
    
    //一個user可擁有很多交易紀錄
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();
    
    //一個user可擁有很多投資
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<Investment> investments = new ArrayList<>();
    
    //一個user可擁有很多財務目標
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<FinancialGoal> financialGoals = new ArrayList<>();
    
	// 一對多(一張問卷有多個題目)
	// mappedBy = "user" : 指定在Question實體中對應的屬性名稱
	// cascade = CascadeType.ALL : 問卷的增刪改操作會自動傳遞到相關的題目
	// orphanRemoval = true : 當題目從問卷中移除時，自動刪除該題目
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 10)
	private List<StrategySetting> strategySettings = new ArrayList<>();
    
    //資料存進DB前，自動設定時間
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    
    //建構子
    public User() {
    }
    		
    // 你原本手寫的 getter/setter (有 lombok 其實可以拿掉，但保留也不會錯)
    public Long getId() { return id; }
    public void setId(Long id){ this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}