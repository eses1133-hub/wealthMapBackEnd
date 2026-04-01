package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.News;
import com.example.demo.repository.NewsRepository;
import com.example.demo.service.NewsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.dto.GNewsResponse;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Value("${gnews.api.key}")
    private String apiKey;
    
    @Autowired
    private NewsService newsService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private NewsRepository newsRepository;

 // 💡 這個 API 變成「同步按鈕」，負責抓資料存進 DB
    @GetMapping("/sync")
    public ResponseEntity<String> syncTopHeadlines() {
        String url = "https://gnews.io/api/v4/top-headlines?country=tw&category=business&apikey=" + apiKey;
        
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            if (response.getBody() != null) {
                GNewsResponse gNewsResponse = mapper.convertValue(response.getBody(), GNewsResponse.class);
                newsService.saveNews(gNewsResponse.getArticles());
                return ResponseEntity.ok("同步完成，資料庫已更新。");
            }
            return ResponseEntity.status(500).body("無法取得 API 資料");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("同步失敗：" + e.getMessage());
        }
    }
    
 // 💡 後台：看全部 (含下架)
    @GetMapping("/admin/list")
    public List<News> getAllNewsForAdmin() {
        return newsService.getAllNewsForAdmin();
    }

    // 💡 前台：只看未被下架的
    @GetMapping("/user/list")
    public List<News> getActiveNewsForUser() {
        return newsService.getActiveNewsForUser();
    }

    @PostMapping("/{id}/hide")
    public ResponseEntity<?> hideNews(@PathVariable("id") Long id, @RequestParam("hide") boolean hide) {
    	newsService.updateHiddenStatus(id, hide);
    	
    	Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("id", id);
        return ResponseEntity.ok(response);
    }
}