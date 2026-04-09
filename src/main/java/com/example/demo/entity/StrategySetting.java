package com.example.demo.entity;

import java.time.LocalDateTime;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "strategy_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StrategySetting {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id" , nullable=false)
	private User user; // 關聯使用者
	private String symbol; // 股票代號 (如: 2330)

	// 使用者自定義的門檻 (例如: 0.05 代表 5%)
	private double buyThreshold; // 負乖離加碼門檻
	private double sellThreshold; // 正乖離減碼門檻

	private boolean isActive; // 是否開啟此提醒
	private LocalDateTime updatedAt;
}
