package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@JsonIgnoreProperties(ignoreUnknown = true) // 💡 加上這行，忽略 lang 等未知欄位
@Data
public class NewsDTO {
    private String id;           // 對應 API 的 "f6af7508..."
    private String title;
    private String description;
    private String content;
    private String url;
    private String image;
    private String publishedAt;
    private SourceDTO source;    // 💡 因為 source 裡面還有東西，所以要再建立一個 SourceDTO

    @Data
    public static class SourceDTO {
        private String id;
        private String name;
        private String url;
    }
}