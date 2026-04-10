package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.News;

import jakarta.transaction.Transactional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
	// 前台用：過濾掉隱藏的新聞，並按時間排序
    List<News> findByIsHiddenFalseOrderByPublishedAtDesc();
    
    // 後台用 : 根據 publishedAt 欄位，由大到小 (Desc, 越新越前面) 排序
    List<News> findAllByOrderByPublishedAtDesc();
    
    boolean existsByApiId(String apiId);
    
}

