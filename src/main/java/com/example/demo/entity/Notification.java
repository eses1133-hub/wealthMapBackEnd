package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tag;      // 例如：[系統]、[個人]
    private String title;    // 標題
    
    @Column(columnDefinition = "TEXT")
    private String content;  // 內容
    
    private LocalDate scheduledDate;//排程時間

    private LocalDateTime createTime = LocalDateTime.now();
}
