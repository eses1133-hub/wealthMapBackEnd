package com.example.demo.repository;

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
}