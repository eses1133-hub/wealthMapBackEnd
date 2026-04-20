package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    
//  💡 後台：看全部 (含下架)
//    @GetMapping("/admin/list")
//    public List<News> getAllNewsForAdmin() {
//        return newsService.getAllNewsForAdmin();
//    }

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
    
//    @GetMapping("/admin/list")
//    public ResponseEntity<Map<String, Object>> getAdminNews(
//        @RequestParam(defaultValue = "0") int page,
//        @RequestParam(defaultValue = "10") int size
//    ) {
//        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
//        Page<News> pageNews = newsRepository.findAll(paging);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("news", pageNews.getContent());       // 當前頁的資料
//        response.put("currentPage", pageNews.getNumber()); // 當前頁碼
//        response.put("totalItems", pageNews.getTotalElements()); // 總筆數
//        response.put("totalPages", pageNews.getTotalPages());   // 總頁數
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
    
    @GetMapping("/admin/list")
    public ResponseEntity<Map<String, Object>> getAdminNews(
        // 💡 設定預設值，如果前端沒傳 page，就從第 0 頁開始
        @RequestParam(value = "page", defaultValue = "0") int page,
        // 💡 如果你想要「看全部」，前端可以傳一個很大的 size (例如 999)
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        // 1. 建立分頁與排序規則
        Pageable paging = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        
        // 2. 從 Repository 抓取分頁資料
        // 注意：這裡 newsRepository.findAll(paging) 會自動處理「含下架」的邏輯（除非你在實體有寫 SQL 標註）
        Page<News> pageNews = newsRepository.findAll(paging);

        // 3. 封裝回傳格式
        Map<String, Object> response = new HashMap<>();
        response.put("news", pageNews.getContent());             // 當前頁的新聞清單
        response.put("currentPage", pageNews.getNumber());       // 目前頁碼
        response.put("totalItems", pageNews.getTotalElements()); // 資料庫總筆數
        response.put("totalPages", pageNews.getTotalPages());     // 總共有幾頁

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    
}