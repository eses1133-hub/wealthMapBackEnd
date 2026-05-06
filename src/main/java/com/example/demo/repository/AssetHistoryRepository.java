package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.AssetHistory;

public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {
	long countByUserId(Long userId);

    // 用來檢查當天是否已有紀錄
	// 🌟 改用 findFirst，避免資料庫有重複時噴報錯
    Optional<AssetHistory>  findFirstByUserIdAndRecordDateOrderByIdDesc(
    		@Param("userId") Long userId, 
            @Param("recordDate") LocalDate recordDate);

    // 用於畫折線圖：按日期升序抓取使用者的歷史紀錄
    List<AssetHistory> findByUserIdOrderByRecordDateAsc(@Param("userId") Long userId);
    
}