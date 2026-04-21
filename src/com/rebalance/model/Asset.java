package com.rebalance.model;

/**
 * 股票資產模型 (Model)
 * 負責存放單一股票的持股狀況並提供基礎運算
 */
public class Asset {
    // 1. 定義屬性 (將其設為 private 是後端開發的專業規範，稱為「封裝」)
    private String name;         // 股票名稱
    private double buyPrice;     // 買入均價 (成本)
    private double currentPrice; // 目前市價
    private int shares;          // 持有股數

    // 2. 建構子 (Constructor)：建立這筆資產時，把資料塞進去
    public Asset(String name, double buyPrice, double currentPrice, int shares) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.currentPrice = currentPrice;
        this.shares = shares;
    }

    // 3. 行為方法 (Methods)：這檔股票自己會算的數據
    
    // 計算這檔股票目前的總市值
    public double getMarketValue() {
        return this.currentPrice * this.shares;
    }

    // 計算這檔股票的總投入成本
    public double getTotalCost() {
        return this.buyPrice * this.shares;
    }

    // 4. Getter 方法：讓 Service (計算大腦) 可以「合法」讀取私有資料
    
    public String getName() {
        return name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public int getShares() {
        return shares;
    }

    public double getBuyPrice() {
        return buyPrice;
    }
}