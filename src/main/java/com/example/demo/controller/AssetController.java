package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Asset;
import com.example.demo.entity.TaiwanStockList;
import com.example.demo.entity.User;
import com.example.demo.repository.TaiwanStockListRepository;
import com.example.demo.service.AssetService;
import com.example.demo.vo.AppResponse;
import com.example.demo.vo.RspCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.List;

@RestController
@RequestMapping("/api/assets") 
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true") // 💡 跨域防護罩開啟
public class AssetController {

    @Autowired
    private AssetService assetService;

	@Autowired
    private TaiwanStockListRepository stockListRepository;
    // ---------------------------------------------------------
    // 1. 新增一筆資產 (前端 POST)
    // ---------------------------------------------------------
    @PostMapping("/{userId}")
    public ResponseEntity<Asset> createAsset(@PathVariable("userId") Long userId, @RequestBody Asset asset) {
        User user = new User();
        user.setId(userId);
        asset.setUser(user);

        Asset savedAsset = assetService.createAsset(asset);
        return ResponseEntity.ok(savedAsset);
    }

    // ---------------------------------------------------------
    // 2. 獲取某個使用者的所有資產 (前端 GET 畫圓餅圖)
    // ---------------------------------------------------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Asset>> getAssetsByUserId(@PathVariable("userId") Long userId) {
        List<Asset> assets = assetService.getAssetsByUserId(userId);
        return ResponseEntity.ok(assets);
    }
    
    // ---------------------------------------------------------
    // 3. 刪除資產 (前端 DELETE)
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable("id") Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok().build(); 
    }
    
	// 輸入股票代碼帶出代碼名稱 by carly
	@GetMapping("/search-stock/{stock_id}")
	public AppResponse<TaiwanStockList> searchStock(@PathVariable("stock_id") String stock_id) {
		return stockListRepository.findById(stock_id)
	            .map(stock -> AppResponse.success(stock))
	            .orElseGet(() -> AppResponse.error(RspCode.NOT_FOUND)); 
	}
}