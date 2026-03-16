package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
	
    @Id	
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //使用者ID

    @Column(nullable = false, length = 100)
    private String name; //使用者名稱

    @Column(nullable = false,unique = true, length = 100)
    private String email; //使用者EMAIL

    @Column(nullable = false, length = 255)
    private String password; //使用者密碼
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    //一個user可擁有很多資產，cascade = CascadeType.ALL=級聯操作
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Asset> assets = new ArrayList<>();
    
    //一個user可擁有很多交易紀錄
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();
    
    //一個user可擁有很多投資
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Investment> investments = new ArrayList<>();
    
  //一個user可擁有很多財務目標
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinancialGoal> financialGoals = new ArrayList<>();
    
    //資料存進DB前，自動設定時間
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    
    //建構子
    public User() {
    }
    		
    public Long getId() {
    	return id;
    }
    
    public void setId(Long id){
    	this.id = id;
    }
    
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }

}