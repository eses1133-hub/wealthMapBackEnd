package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
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

	//這筆資產目前的金額，不能空白
	@Column(nullable = false)
	private Double amount;

	//記錄這筆資產建立的時間
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	
	//很多筆Asset屬於一個User
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	//第一次存進資料庫之前，自動設定 createdAt
	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}
	
	public Asset() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

