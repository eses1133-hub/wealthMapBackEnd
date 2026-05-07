package com.example.demo.repository;

import com.example.demo.entity.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, Long> {
    
    // 讓組員以後可以直接用 UserID 撈出所有收支明細
    List<CashFlow> findByUserId(Long userId);
}