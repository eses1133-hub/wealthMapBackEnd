package com.example.demo.service;

import com.example.demo.entity.Asset;
import com.example.demo.dto.AssetDTO;
import com.example.demo.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import com.example.demo.entity.Asset;
import com.example.demo.repository.AssetRepository;

import java.util.List;

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
 // 🌟 4. 修改資產資料 (新增這段)
    public Asset updateAsset(Long id, AssetDTO assetDTO) {
        // 第一步：先用 ID 把資料庫裡的「舊資產」撈出來。如果找不到就拋出錯誤。
        Asset existingAsset = assetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到這筆資產，ID: " + id));

        // 第二步：把使用者傳來的新名字和新金額，更新到舊資產上
        existingAsset.setName(assetDTO.name());
        existingAsset.setAmount(assetDTO.amount());

        // 防呆機制：如果是股票，順便把總成本也更新一下 (如果你們有用到這個欄位的話)
        if (assetDTO.totalCost() != null) {
            existingAsset.setTotalCost(assetDTO.totalCost());
        }

        // 第三步：存回資料庫，Spring Data JPA 看到 ID 存在，就會自動幫你執行 UPDATE 語法
        return assetRepository.save(existingAsset);
    }
}