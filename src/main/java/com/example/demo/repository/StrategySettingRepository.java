package com.example.demo.repository;

import com.example.demo.entity.StrategySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StrategySettingRepository extends JpaRepository<StrategySetting, Long> {
	// 找出所有「啟用的」且「特定股票」的設定，這樣 14:00 才能針對這支股票通知所有人
    List<StrategySetting> findBySymbolAndIsActiveTrue(String symbol);
    
    // 找出所有「啟用的」且「特定股票」的設定，這樣 14:00 才能針對這支股票通知所有人
    List<StrategySetting> findByUserId(Long userId);
    
    // 用於新增時檢查是否重複設定
    boolean existsByUserIdAndSymbol(Long userId, String symbol);
}
