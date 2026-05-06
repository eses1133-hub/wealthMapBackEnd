package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.AssetHistory;
import com.example.demo.service.AssetService;

@RestController
@RequestMapping("/api/asset-history") // 💡 使用新的路徑
public class AssetHistoryController {

    @Autowired
    private AssetService assetService;

    /**
     * 1. 獲取折線圖趨勢數據
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<AssetHistory>> getTrend(@PathVariable("userId") Long userId) {
        List<AssetHistory> trendData = assetService.getAssetTrend(userId);
        return ResponseEntity.ok(trendData);
    }

    /**
     * 2. 手動觸發快照更新 (選用)
     * 當前端「我的資產」操作完跳回首頁前，可以 call 這一支
     */
    @PostMapping("/sync/{userId}")
    public ResponseEntity<?> triggerSync(@PathVariable("userId") Long userId) {
        assetService.syncHistory(userId);
        return ResponseEntity.ok().build();
    }
}
