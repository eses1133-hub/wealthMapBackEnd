package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.GNewsResponse;
import com.example.demo.dto.NewsDTO;
import com.example.demo.entity.News;
import com.example.demo.repository.NewsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Value("${gnews.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    // 💡 1. 核心同步邏輯 (封裝起來，讓大家都能用)
    public void executeSync() {
        String url = "https://gnews.io/api/v4/top-headlines?country=tw&category=business&apikey=" + apiKey;
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            if (response.getBody() != null) {
                GNewsResponse gNewsResponse = mapper.convertValue(response.getBody(), GNewsResponse.class);
                
                // 呼叫你原本寫好的 saveNews 存入資料庫並去重
                this.saveNews(gNewsResponse.getArticles()); 
                System.out.println("⏰ [" + java.time.LocalDateTime.now() + "] 自動同步成功！");
            }
        } catch (Exception e) {
            System.err.println("❌ 自動同步失敗：" + e.getMessage());
        }
    }

    // 💡 2. 定時排程：每小時執行一次
    @Scheduled(cron = "0 0 * * * *") 
    public void autoSync() {
        System.out.println("🚀 排程啟動：開始抓取最新新聞...");
        executeSync();
    }
 // 💡 加上這個方法，解決 Undefined 錯誤
    @Transactional
    public void updateHiddenStatus(Long id, boolean hidden) {
        // 1. 從資料庫找出該筆新聞
        Optional<News> optionalNews = newsRepository.findById(id);
        
        if (optionalNews.isPresent()) {
            News news = optionalNews.get();
            // 2. 修改隱藏狀態
            news.setHidden(hidden);
            // 3. 存回資料庫 (這行最重要，沒這行重整資料會不見)
            newsRepository.save(news);
            System.out.println("ID: " + id + " 已成功更新狀態為: " + hidden);
        } else {
            // 如果找不到 ID 的處理
            throw new RuntimeException("找不到編號為 " + id + " 的新聞");
        }
    }
    	@Transactional
    	public void saveNews(List<NewsDTO> dtoList) {
    	    if (dtoList == null) return;

    	    for (NewsDTO dto : dtoList) {
    	        // 關鍵改動：GNews 沒有 id，所以我們用網址產生一個唯一的 Hash ID
    	        // 這樣就不會因為 dto.getId() 是 null 而存不進去
    	        String uniqueApiId = String.valueOf(dto.getUrl().hashCode());

    	        // 1. 檢查重複
    	        if (!newsRepository.existsByApiId(uniqueApiId)) {
    	            News news = new News();
    	            news.setApiId(uniqueApiId); // 這裡改用產生的 ID
    	            news.setTitle(dto.getTitle());
    	            news.setDescription(dto.getDescription());
    	            news.setContent(dto.getContent());
    	            news.setUrl(dto.getUrl());
    	            news.setImage(dto.getImage());
    	            news.setPublishedAt(dto.getPublishedAt());
    	            
    	            if (dto.getSource() != null) {
    	                news.setSourceName(dto.getSource().getName());
    	                news.setSourceUrl(dto.getSource().getUrl());
    	            }

    	            newsRepository.save(news);
    	            System.out.println("✅ 成功存入新新聞: " + dto.getTitle());
                } else {
                    System.out.println("⏩ 跳過重複新聞: " + dto.getTitle());
                }
    	    }
    	}
    	
    	public List<News> getAllNewsForAdmin() {
            return newsRepository.findAllByOrderByPublishedAtDesc();
        }

        public List<News> getActiveNewsForUser() {
            return newsRepository.findByIsHiddenFalseOrderByPublishedAtDesc();
        }

        @Transactional
        public void toggleNewsVisibility(Long id, boolean hide) {
            News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到新聞 ID: " + id));
            news.setHidden(hide); 
            newsRepository.save(news);
        }
}
