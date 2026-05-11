package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "liabilities")
@Data
public class Liability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;      // 項目名稱 (例如：XX銀行房貸、車貸)

    @Column(nullable = false)
    private String category;  // 分類 (例如：房貸、車貸、信貸、學貸)

    @Column(nullable = false)
    private Double amount;    // 負債金額
    
    @Column(nullable = true)
    private Double monthlyPayment;
 
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore //一開始就把防護罩加上，杜絕無限迴圈！
    private User user;

    // --- 自動產生 Getters 和 Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

	}
