package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rebalance_settings")
public class RebalanceSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String symbol;

    @Column(name = "target_percentage")
    private double targetPercentage;

    @Column(name = "current_shares")
    private int currentShares;

    @Column(name = "is_active")
    private boolean isActive = true;

    // --- Getter 和 Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public double getTargetPercentage() { return targetPercentage; }
    public void setTargetPercentage(double targetPercentage) { this.targetPercentage = targetPercentage; }

    public int getCurrentShares() { return currentShares; }
    public void setCurrentShares(int currentShares) { this.currentShares = currentShares; }

    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
}