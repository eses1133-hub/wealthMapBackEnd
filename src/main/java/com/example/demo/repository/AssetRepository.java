package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
	// 找出所有類型為 'stock' 的資產，並只回傳不重複的股票代號
    @Query("SELECT DISTINCT a.symbol FROM Asset a WHERE a.type = 'stock'")
    List<String> findDistinctStockSymbols();
    
    List<Asset> findByUserId(@Param("userId")Long userId);
    

    @Query("SELECT a.symbol FROM Asset a WHERE a.user.id = :userId ")
    List<String> findSymbolsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a.symbol FROM Asset a " +
    	       "WHERE a.user.id = :userId " +
    	       "AND NOT EXISTS (" +
    	       "    SELECT s FROM StrategySetting s " +
    	       "    WHERE s.user.id = :userId AND s.symbol = a.symbol" +
    	       ")")
	List<String> findAvailableSymbolsByUserId(@Param("userId") Long userId);
    
    //這是用來計算計使用者的資產總和的 (用在首頁折線圖
    @Query("SELECT SUM(a.amount) FROM Asset a WHERE a.user.id = :userId")
    Double sumAmountByUserId(@Param("userId")Long userId);
}