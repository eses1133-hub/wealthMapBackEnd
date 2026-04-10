package com.example.demo.entity;


import java.time.LocalDate;
import java.time.LocalDateTime;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="user_id",nullable = false)
	private Long userId; //誰的通知
	
    // 🔥 分類（系統 / 個人）
    private String tag;

    // 🔥 標題
    private String title;

	@Column(columnDefinition = "TEXT")
	private String content; //誰的內容
	
	private Boolean isRead = false;; //是否已讀
	
	@Column(name = "created_at",nullable = false)
	private LocalDateTime createdAt;  //建立時間
	
	@Column(nullable = false)
	private LocalDate scheduledDate;
	
	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

}