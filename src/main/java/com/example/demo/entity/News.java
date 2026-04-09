package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 資料庫自增 ID

    @Column(name = "api_id", unique = true, nullable = false)
    private String apiId; // API 的 Hash ID

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "content", length = 1000)
    private String content;

    @Column(name = "url", length = 1000)
    private String url;
    
    @Column(name = "image", length = 1000)
    private String image;
    
    private String publishedAt;
    
    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden = false; // 預設為顯示 (false)

    // 💡 處理巢狀的 source 資料
    private String sourceName; // 存入 "ftnn.com.tw"
    private String sourceUrl;  // 存入 "https://www.ftnn.com.tw"
    
 // 💡 手動增加 Setter 確保與 Service 同步，避免 Lombok 的命名誤會
    public void setHidden(boolean hidden) {
        this.isHidden = hidden;
    }
}