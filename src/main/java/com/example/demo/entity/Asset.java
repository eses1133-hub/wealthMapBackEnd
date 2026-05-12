package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	//資產名稱不能空白，最長100字
	@Column(nullable = false, length = 100)
	private String name;

	//資產種類，不能空白
	@Column(nullable = false, length = 50)
	private String type;
	
	//證券代碼，股票的代碼、基金的代碼等，最長100字
	@Column(name = "symbol", length = 100)
	private String symbol;

	//這筆資產目前的金額，不能空白
	@Column(nullable = false)
	private Double amount;
	
	@Column(name = "shares")
    private Double shares;

    // 總成本 (現金類資產此欄位為 null)
	@Column(name = "cost")
	private Double cost;

	//記錄這筆資產建立的時間
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	
	//很多筆Asset屬於一個User
	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;

	//第一次存進資料庫之前，自動設定 createdAt
	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}
	public Double getCost() {
	    return cost;
	}
	public void setCost(Double cost) {
	    this.cost = cost;
    }
}

