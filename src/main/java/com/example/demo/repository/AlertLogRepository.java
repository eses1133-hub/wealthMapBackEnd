package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.AlertLog;
import com.example.demo.entity.AlertLog.AlertCategory;
import com.example.demo.entity.AlertLog.NotificationChannel;
import com.example.demo.entity.User;

public interface AlertLogRepository extends JpaRepository<AlertLog, Long>{
	/**
     * 檢查特定時間後，該使用者、該股票標的是否已有特定分類的提醒紀錄。
     * 這是為了防止「同一天」發送重複的加減碼通知。
     * * @param userId 使用者 ID
     * @param targetId 股票代號 (例如 2330.TPE)
     * @param category 提醒分類 (例如 STOCK_STRATEGY)
     * @param time 啟始時間 (通常傳入當天的 00:00:00)
     * @return boolean 是否已存在紀錄
     */
	boolean existsByUserIdAndTargetIdAndCategoryAndAlertTimeAfter(
			Long userId, 
            String targetId, 
            AlertCategory category, 
            LocalDateTime time
            );
	/**
     * 找出某個使用者的所有提醒歷史 (按時間降冪排序，最新的在前)
     */
    List<AlertLog> findByUserIdOrderByAlertTimeDesc(Long userId);

    /**
     * 找出所有發送失敗的紀錄 (未來可以用來做手動補發功能)
     */
    List<AlertLog> findByStatus(AlertLog.AlertStatus status);
    
    /**
     * 撈取某個使用者最近的 10 筆網頁通知
     */
    List<AlertLog> findTop10ByUserAndChannelOrderByAlertTimeDesc(User user, NotificationChannel channel);
}
