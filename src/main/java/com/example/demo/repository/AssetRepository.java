package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Asset;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
	// 找出所有類型為 'stock' 的資產，並只回傳不重複的股票代號
    @Query("SELECT DISTINCT a.symbol FROM Asset a WHERE a.type = 'STOCK'")
    List<String> findDistinctStockSymbols();
    
    List<Asset> findByUserId(@Param("userId")Long userId);
    

    @Query("SELECT a.symbol FROM Asset a WHERE a.user.id = :userId ")
    List<String> findSymbolsByUserId(@Param("userId") Long userId);
    
    //加減碼策略，新增時的下拉選單。
    @Query("SELECT DISTINCT a.symbol FROM Asset a " +
    	       "WHERE a.user.id = :userId " +
    	       "AND a.type = 'STOCK' " +
    	       "AND a.symbol is not null " +
    	       "AND NOT EXISTS (" +
    	       "    SELECT s FROM StrategySetting s " +
    	       "    WHERE s.user.id = :userId AND s.symbol = a.symbol" +
    	       ")")
	List<String> findAvailableSymbolsByUserId(@Param("userId") Long userId);
    
    //這是用來計算計使用者的資產總和的 (用在首頁折線圖
    @Query("SELECT SUM(a.amount) FROM Asset a WHERE a.user.id = :userId")
    Double sumAmountByUserId(@Param("userId")Long userId);
    
    
    // 更新資產中的股票成本價為現價 set amount	
    @Modifying
    @Transactional
    @Query(value = "UPDATE assets a " +
                   "JOIN stock_price sp ON a.symbol = sp.symbol " +
                   "SET a.amount = sp.close_price * a.shares " +
                   "WHERE a.type = 'STOCK' " +
                   "AND a.shares is not null " +
                   "AND sp.date = (SELECT MAX(date) FROM stock_price WHERE symbol = a.symbol)", 
           nativeQuery = true)
    void updateStockAssetsAmount();
        
    // 資產再平衡：新增時的下拉選單，排除已存在於 RebalanceSetting 的標的
    @Query("SELECT DISTINCT a.symbol FROM Asset a " +
           "WHERE a.user.id = :userId " +
           "AND a.type = 'STOCK' " +
           "AND a.symbol IS NOT NULL " +
           "AND NOT EXISTS (" +
           "    SELECT r FROM RebalanceSetting r " +
           "    WHERE r.userId = :userId AND r.symbol = a.symbol" +
           ")")
    List<String> findRebalanceAvailableSymbolsByUserId(@Param("userId") Long userId);
    
    
}