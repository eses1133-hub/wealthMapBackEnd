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
    //  4. 修改資產資料
    public Asset updateAsset(Long id, AssetDTO assetDTO) {
        Asset existingAsset = assetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到這筆資產，ID: " + id));

        existingAsset.setName(assetDTO.name());
        existingAsset.setType(assetDTO.type());

        // ✅ 防呆：amount 為 null 時用 totalCost 頂替
        Double finalAmount = assetDTO.amount();
        if (finalAmount == null) {
            finalAmount = assetDTO.totalCost();
        }
        existingAsset.setAmount(finalAmount);

        // ✅ 股票/基金相關欄位
        if (assetDTO.stockId() != null) {
            existingAsset.setSymbol(assetDTO.stockId());
        }
        if (assetDTO.sharesOwned() != null) {
            existingAsset.setShares(assetDTO.sharesOwned());
        }
        if (assetDTO.totalCost() != null) {
            existingAsset.setTotalCost(assetDTO.totalCost());
        }

        return assetRepository.save(existingAsset);
    }
}