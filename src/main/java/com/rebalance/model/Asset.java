package com.rebalance.model;

public class Asset {
    private String symbol;
    private String name;
    private double currentPrice;
    private int sharesOwned;
    private double targetPercentage;
    private String suggestion; // 用來存計算結果

    // Getter 和 Setter (一定要有，否則 Jackson 轉換會失敗)
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public int getSharesOwned() { return sharesOwned; }
    public void setSharesOwned(int sharesOwned) { this.sharesOwned = sharesOwned; }
    public double getTargetPercentage() { return targetPercentage; }
    public void setTargetPercentage(double targetPercentage) { this.targetPercentage = targetPercentage; }
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
}