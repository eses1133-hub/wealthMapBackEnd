package com.example.demo.service;

import com.example.demo.entity.Asset;
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
}