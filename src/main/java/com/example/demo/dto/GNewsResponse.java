package com.example.demo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GNewsResponse {
    
    // API 回傳符合搜尋條件的總則數
    private int totalArticles;
    
    // 💡 關鍵：這裡的變數名稱必須跟 JSON 裡的 "articles" 一模一樣
    private List<NewsDTO> articles;
}