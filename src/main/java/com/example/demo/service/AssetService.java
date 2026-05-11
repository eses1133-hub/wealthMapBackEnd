package com.example.demo.service;

import com.example.demo.entity.Asset;
import com.example.demo.entity.AssetHistory;
import com.example.demo.entity.User;
import com.example.demo.repository.AssetHistoryRepository;
import com.example.demo.dto.AssetDTO;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.example.demo.entity.Asset;
import com.example.demo.repository.AssetRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    // 1. 新增資產
    public Asset createAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    // 2. 查詢該使用者的所有資產
    public List<Asset> getAssetsByUserId(Long userId) {
        return assetRepository.findByUserId(userId);
    }

    // 3. 刪除資產
    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }

    // 以下是用在首頁折線圖的相關方法
    @Autowired
    private AssetHistoryRepository historyRepository;

    // 4. 更新當日資產總額
    @Transactional
    public synchronized void syncHistory(Long userId) {
        // 使用 Optional 處理 null，避免 Lambda 報錯 (effectively final 問題)
        final Double currentTotal = Optional.ofNullable(assetRepository.sumAmountByUserId(userId)).orElse(0.0);
        LocalDate today = LocalDate.now();

        // 檢查是否已有任何歷史紀錄
        boolean hasNeverRecorded = historyRepository.countByUserId(userId) == 0;

        // 🌟 關鍵邏輯：如果總額是 0 且從來沒存過，就直接跳過不存
        if (currentTotal == 0 && hasNeverRecorded) {
            return;
        }

        historyRepository.findFirstByUserIdAndRecordDateOrderByIdDesc(userId, today)
                .ifPresentOrElse(
                        history -> {
                            history.setTotalAmount(currentTotal);
                            historyRepository.save(history);
                            System.out.println("User " + userId + " 今日已存在，執行更新。");
                        },
                        () -> {
                            AssetHistory newHistory = new AssetHistory(userId, currentTotal, today);
                            historyRepository.save(newHistory);
                            System.out.println("User " + userId + " 今日無紀錄，執行新增。");
                        });
    }

    @Autowired
    private UserRepository userRepository;

    // 新增排程方法：每天凌晨 0 點觸發
    @Scheduled(cron = "0 0 0 * * *")
    public void dailyAutoSync() {
        // 找出系統中所有使用者
        List<User> allUsers = userRepository.findAll();

        // 幫每個使用者呼叫你寫好的優雅邏輯
        for (User user : allUsers) {
            this.syncHistory(user.getId());
        }
        System.out.println("每日資產自動快照執行完成！執行人數：" + allUsers.size());
    }

    // --- 5. 供前端折線圖使用的-取得歷史紀錄的方法
    public List<AssetHistory> getAssetTrend(Long userId) {
        return historyRepository.findByUserIdOrderByRecordDateAsc(userId);
    }    
 // 🌟 4. 修改資產資料 (新增這段)
    public Asset updateAsset(Long id, AssetDTO assetDTO) {
        // 第一步：先用 ID 把資料庫裡的「舊資產」撈出來。如果找不到就拋出錯誤。
        Asset existingAsset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到這筆資產，ID: " + id));

        // 第二步：把使用者傳來的新名字和新金額，更新到舊資產上
        existingAsset.setName(assetDTO.name());
        existingAsset.setAmount(assetDTO.amount());

        // 防呆機制：如果是股票，順便把總成本也更新一下 (如果你們有用到這個欄位的話)
        if (assetDTO.cost() != null) {
            existingAsset.setCost(assetDTO.cost());
        }

        // 第三步：存回資料庫，Spring Data JPA 看到 ID 存在，就會自動幫你執行 UPDATE 語法
        return assetRepository.save(existingAsset);
    }
}