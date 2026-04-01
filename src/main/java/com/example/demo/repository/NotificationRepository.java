package com.example.demo.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 💡 讓最新的通知排在最前面
    List<Notification> findAllByOrderByCreateTimeDesc();
    
 // 💡 只抓取排程時間「小於等於」現在時間的公告，並按時間倒序
    List<Notification> findByScheduledDateLessThanEqualOrderByScheduledDateDesc(LocalDateTime now);
    
    // 如果是後台管理員，可能需要看「全部」包括未來的
    List<Notification> findAllByOrderByScheduledDateDesc();
    
    
 // 💡 算出目前「已發布」的公告總數 (用於相減邏輯)
//    long countByScheduledDateLessThanEqual(LocalDateTime now);
 // 算出「日期 <= 今天」的總數 (用於紅點計算)
    long countByScheduledDateLessThanEqual(LocalDate now);
    
    // 抓取「日期 <= 今天」的清單 (用於前端顯示)
    List<Notification> findByScheduledDateLessThanEqualOrderByScheduledDateDesc(LocalDate now);

}