package com.example.demo.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "alert_logs")
@Data
public class AlertLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
	@JsonIgnore
    private User user;

	@Enumerated(EnumType.STRING)
	private AlertCategory category; // 提醒分類：STOCK_STRATEGY, ASSET_REBALANCE, SYSTEM_INFO

	private String title; // 提醒標題 (例如：[加碼提醒] 台積電)

	@Column(columnDefinition = "TEXT")
	private String content; // 提醒詳細內容 (JSON 格式或純文字)

	private String targetId; // 關聯的 ID (例如股票代號 "2330" 或 某筆交易 ID)

	private LocalDateTime alertTime; // 發送時間

	@PrePersist
	public void prePersist() {
		this.alertTime = LocalDateTime.now();
	}

	
	@Enumerated(EnumType.STRING)
	private NotificationChannel channel; // 發送管道：EMAIL, LINE, WEB_PUSH

	private boolean isRead; // 使用者是否在 App 內讀取了
	
	@Enumerated(EnumType.STRING)
    private AlertStatus status = AlertStatus.PENDING; // 預設為「待處理 (PENDING)」
	
	@Column(columnDefinition = "TEXT")
    private String errorMessage; // 紀錄失敗原因

    private int retryCount = 0; // 重試次數
	

    
	//分類定義
	public enum AlertCategory {
	 STOCK_STRATEGY, // 股票加減碼
	 ASSET_REBALANCE, // 資產再平衡
	 SYSTEM_NOTICE    // 系統通知
	}

	public enum NotificationChannel {
	 EMAIL, LINE, WEB_PUSH
	}
	
	public enum AlertStatus {
        PENDING,   // 待發送 (已塞入 Log，標記為未寄送)
        SENT,      // 已寄送 (發送成功)
        FAILED     // 發送失敗
    }
}

