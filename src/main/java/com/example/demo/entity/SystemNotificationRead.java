package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "system_notification_reads")
@Data
public class SystemNotificationRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; 

    @Column(name = "notification_id")
    private Long notificationId; 

    // 💡 有這行就夠了！這筆資料存在 = 已讀，時間 = 讀取時間
    private LocalDateTime readAt = LocalDateTime.now(); 
}
