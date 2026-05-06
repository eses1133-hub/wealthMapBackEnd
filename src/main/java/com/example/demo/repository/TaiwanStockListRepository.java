package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TaiwanStockList;
@Repository
public interface TaiwanStockListRepository extends JpaRepository<TaiwanStockList, String> {
	// 基礎的 saveAll, findById 等功能已經內建
	// 根據代碼搜尋股票，可以增加：
	List<TaiwanStockList> findByStockId(String stockId);

	// 模糊搜尋：根據代碼開頭查詢 (例如輸入 23 找 23xx)
	List<TaiwanStockList> findByStockIdStartingWith(String stockId);

	// 或者根據名稱模糊搜尋
	List<TaiwanStockList> findByStockNameContaining(String stockName);
}