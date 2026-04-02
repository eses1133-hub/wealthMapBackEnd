package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Notifications")
@Setter
@Getter
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="user_id",nullable = false , length = 50)
	private Long userId; //誰的通知
	
	@Column(nullable=false,length = 100)
	private String content; //誰的內容
	
	@Column(nullable=false)
	private Boolean isRead; //是否已讀
	
	@Column(name = "created_at",nullable = false)
	private LocalDateTime createdAt;  //建立時間
	
	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}
}
