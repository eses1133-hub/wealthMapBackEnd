package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Liability;
import com.example.demo.repository.LiabilityRepository;
import java.util.List;

@Service
public class LiabilityService {

    @Autowired
    private LiabilityRepository liabilityRepo;

    // 新增負債
    public Liability createLiability(Liability liability) {
        return liabilityRepo.save(liability);
    }

	// 取得某使用者的所有負債
    public List<Liability> getLiabilitiesByUserId(Long userId) {
        return liabilityRepo.findByUser_Id(userId);
    }

    // 刪除負債
    public void deleteLiability(Long id) {
        liabilityRepo.deleteById(id);
    }
    
    // 修改負債資料 
    public Liability updateLiability(Long id, Liability dto) {
        Liability existing = liabilityRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到負債，ID: " + id));
        existing.setName(dto.getName());
        existing.setCategory(dto.getCategory());
        existing.setAmount(dto.getAmount());
        existing.setMonthlyPayment(dto.getMonthlyPayment());
        existing.setNotifyEnabled(dto.getNotifyEnabled());
        existing.setDueDay(dto.getDueDay());
        return liabilityRepo.save(existing);
    }
    
    // 計算貸款剩餘期數
    public double calculateRemainingMonths(Liability liability) {
        // 安全檢查：避免除以 0 或空值
        if (liability.getMonthlyPayment() <= 0 || liability.getAmount() == null || liability.getAmount() <= 0) {
            return 1;
        }

        // 計算剩餘期數：總額 / 每月還款量 (使用 Math.ceil 無條件進位)
        return Math.ceil(liability.getAmount() / liability.getMonthlyPayment());
    }
}