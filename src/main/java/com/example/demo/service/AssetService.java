package com.example.demo.service;

import com.example.demo.entity.Asset;
import com.example.demo.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public Asset createAsset(Asset asset) {
        // 在存入資料庫前，可以先做一些防呆檢查或自動補全
        
        // 呼叫 Repository 存入資料庫
        return assetRepository.save(asset);
    }

    // 2. 獲取某個使用者的所有資產
    public List<Asset> getAssetsByUserId(Long userId) {
        return assetRepository.findByUser_Id(userId);
    }
    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }
}