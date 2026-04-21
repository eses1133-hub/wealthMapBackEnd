package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FinancialGoal;

@Repository // 💡 標示這是一個資料庫連線庫
// 💡 繼承 JpaRepository 就會自動獲得 save() 和 deleteById() 等基本功能
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {
	
    // 💡 我們自己新增的客製化功能：靠著 userId 去把這個人的所有目標都找出來
    List<FinancialGoal> findByUserId(Long userId);
    
}