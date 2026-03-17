package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "investments")
public class Investment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	//投資標的的代號或名稱
	@Column(nullable = false, length = 100)
	private String symbol;

	//投資類型
	@Column(nullable = false, length = 50)
	private String type;

	//持有數量
	@Column(nullable = false)
	private Double quantity;

	//買入價格
	@Column(name = "buy_price", nullable = false)
	private Double buyPrice;

	//目前價格
	@Column(name = "current_price", nullable = false)
	private Double currentPrice;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public Investment() {
	}

	public Long getId() {
		return id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(Double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public Double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(Double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
