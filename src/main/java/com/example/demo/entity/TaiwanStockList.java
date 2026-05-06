package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "taiwan_stock_list", indexes = {@Index(columnList = "stock_id")})
@Data
@NoArgsConstructor // 自動生成無參數建構子
@AllArgsConstructor // 自動生成全參數建構子
public class TaiwanStockList {
    
    @Id
    @Column(name = "stock_id", length = 20)
    private String stockId; // 股票代碼 (主鍵)

    @Column(name = "stock_name", nullable = false)
    private String stockName; // 股票名稱

    @Column(name = "industry_category")
    private String industryCategory; // 產業類別 (選填，方便分類)

    @Column(name = "update_time")
    private LocalDateTime updateTime; // 記錄最後更新時間

}

