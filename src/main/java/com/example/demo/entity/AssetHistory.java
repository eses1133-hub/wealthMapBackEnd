package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

//用在前端首頁折線圖
@Entity
@Table(name = "asset_history", uniqueConstraints = {
	    @UniqueConstraint(name = "uk_user_record_date", columnNames = {"userId", "record_date"})
	})
@Data
@NoArgsConstructor
public class AssetHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Double totalAmount;

    // 使用 LocalDate，因為我們只需要存到「天」，不需要時分秒
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    public AssetHistory(Long userId, Double totalAmount, LocalDate recordDate) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.recordDate = recordDate;
    }
}
