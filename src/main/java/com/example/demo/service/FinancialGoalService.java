package com.example.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.FinancialGoal;
import com.example.demo.repository.FinancialGoalRepository;

@Service
public class FinancialGoalService {

    @Autowired
    private FinancialGoalRepository goalRepository;

    // 1. 新增目標
    public FinancialGoal createGoal(FinancialGoal goal) {
        return goalRepository.save(goal);
    }

    // 2. 查詢該使用者的所有目標
    public List<FinancialGoal> getGoalsByUserId(Long userId) {
        return goalRepository.findByUserId(userId); 
    }

    // 3. 刪除目標
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }
    
    public FinancialGoal updateGoal(Long id, FinancialGoal dto) {
        FinancialGoal existing = goalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到目標，ID: " + id));
        existing.setGoalName(dto.getGoalName());
        existing.setTargetAmount(dto.getTargetAmount());
        existing.setTargetDate(dto.getTargetDate());
        existing.setAssetId(dto.getAssetId()); // 更新綁定資產
        return goalRepository.save(existing);
    }
    
}