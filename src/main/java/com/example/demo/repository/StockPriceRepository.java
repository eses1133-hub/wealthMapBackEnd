package com.example.demo.repository;

import com.example.demo.entity.StockPrice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
	// 根據代碼計算資料庫存量
	long countBySymbol(String symbol);
	
	// 檢查資料庫是否已存在該代號與日期
    boolean existsBySymbolAndDate(String symbol, LocalDate date);
    
    // 依據日期降冪排列，取出前 20 筆（最新的 20 天）
    List<StockPrice> findTop20BySymbolOrderByDateDesc(String symbol);
    
    
    Optional<StockPrice> findFirstBySymbolOrderByDateDesc(String symbol);
    
    //使用Optional可以維持唯一性，逼迫程式處理「找不到資料」的情況，避免 NullPointerException。
    Optional<StockPrice> findBySymbolAndDate(String symbol, LocalDate date);
}
