package com.example.demo.controller;

import com.example.demo.entity.Asset;
import com.example.demo.entity.User;
import com.example.demo.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets") 
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AssetController {

    @Autowired
    private AssetService assetService;

    // ---------------------------------------------------------
    // 1. 新增一筆資產 (前端會用 POST 方法丟 JSON 過來)
    // 網址：POST http://localhost:8080/api/assets/{userId}
    // ---------------------------------------------------------
    @PostMapping("/{userId}")
    public ResponseEntity<Asset> createAsset(@PathVariable("userId") Long userId, @RequestBody Asset asset) {
        User user = new User();
        user.setId(userId);
        asset.setUser(user);

        // 呼叫 Service 把這筆資料存進資料庫
        Asset savedAsset = assetService.createAsset(asset);
        return ResponseEntity.ok(savedAsset);
    }

    // ---------------------------------------------------------
    // 2. 獲取某個使用者的所有資產 (前端畫圓餅圖會用 GET 呼叫這個)
    // 網址：GET http://localhost:8080/api/assets/user/{userId}
    // ---------------------------------------------------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Asset>> getAssetsByUserId(@PathVariable("userId") Long userId) {
        List<Asset> assets = assetService.getAssetsByUserId(userId);
        return ResponseEntity.ok(assets);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable("id") Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok().build(); // 回傳 200 OK 代表刪除成功
    }

}