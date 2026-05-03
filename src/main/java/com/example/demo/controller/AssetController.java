package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Asset;
import com.example.demo.entity.User;
import com.example.demo.service.AssetService;
import com.example.demo.service.StockService; // 🌟 引入股票查詢服務
import com.example.demo.dto.AssetDTO; 
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.dto.TwStockListDTO;
import com.example.demo.service.StockReferenceService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assets") 
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true") 
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private StockReferenceService stockRefService;
    
    
    
    
    // 0. 查詢股票名稱 (前端輸入代號離開時觸發)
    @GetMapping("/search-stock/{symbol}")
    public ApiResponseDTO<TwStockListDTO> searchStock(@PathVariable("symbol") String symbol) {
        TwStockListDTO stockData = stockRefService.getStockNameInfo(symbol);
        return new ApiResponseDTO<>(200, "操作成功", stockData);
    }

    // 1. 新增一筆資產 (前端 POST)
    @PostMapping("/{userId}")
    public ResponseEntity<AssetDTO> createAsset(@PathVariable("userId") Long userId, @RequestBody AssetDTO assetDTO) {
        
        // 將 DTO 轉為 Entity
        Asset asset = new Asset();
        asset.setName(assetDTO.name()); 
        asset.setType(assetDTO.type());
        asset.setAmount(assetDTO.amount());
        asset.setSymbol(assetDTO.stockId());
        asset.setShares(assetDTO.sharesOwned());
        asset.setTotalCost(assetDTO.totalCost());

        User user = new User();
        user.setId(userId);
        asset.setUser(user);

        // 存入資料庫
        Asset savedAsset = assetService.createAsset(asset);

        // 將 Entity 轉回 DTO
        AssetDTO savedDTO = new AssetDTO(
            savedAsset.getId(),
            savedAsset.getName(),
            savedAsset.getType(),
            savedAsset.getAmount(),
            savedAsset.getSymbol(),
            savedAsset.getShares(),
            savedAsset.getTotalCost()
        );

        return ResponseEntity.ok(savedDTO);
    }

    // 獲取某個使用者的所有資產
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AssetDTO>> getAssetsByUserId(@PathVariable("userId") Long userId) {
        
        List<Asset> assets = assetService.getAssetsByUserId(userId);
        
        // 將 List<Asset> 轉為 List<AssetDTO>
        List<AssetDTO> assetDTOs = assets.stream()
            .map(asset -> new AssetDTO(
                asset.getId(),
                asset.getName(),
                asset.getType(),
                asset.getAmount(),
                asset.getSymbol(), 
                asset.getShares(),asset.getTotalCost()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(assetDTOs);
    }
    
    // ---------------------------------------------------------
    // 3. 刪除資產 (前端 DELETE)
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable("id") Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok().build(); 
    }
}