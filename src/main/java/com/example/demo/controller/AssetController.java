package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Asset;
import com.example.demo.entity.User;
import com.example.demo.service.AssetService;
import com.example.demo.service.StockService; 
import com.example.demo.dto.StrategyDTO;
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
    
    @Autowired
    private StockService stockService;
    
    
    // 0. 查詢股票名稱 (前端輸入代號離開時觸發)
    @GetMapping("/search-stock/{symbol}")
    public ApiResponseDTO<TwStockListDTO> searchStock(@PathVariable("symbol") String symbol) {
        
        TwStockListDTO nameData = stockRefService.getStockNameInfo(symbol);

        Double currentPrice = null; // 預設為 null
        try {
            // 呼叫組員寫好的快速報價功能
            StrategyDTO quote = stockService.getQuickQuote(symbol);
            if (quote != null) {
                currentPrice = quote.getCurrentPrice(); // 把股價抓出來
            }
        } catch (Exception e) {
            // 【防護罩】如果隊友的 API 暫時抓不到或出錯，只在後台印出錯誤，不要讓整個網頁當機
            System.out.println(">>> 呼叫隊友 API 抓取股價失敗：" + e.getMessage());
        }

        TwStockListDTO finalData = new TwStockListDTO(
            nameData.stockId(),
            nameData.stockName(),
            nameData.industryCategory(),
            nameData.updateTime(),
            currentPrice 
        );

        return new ApiResponseDTO<>(200, "操作成功", finalData);
    }
    // 1. 新增一筆資產 (前端 POST)
    @PostMapping("/{userId}")
    public ResponseEntity<AssetDTO> createAsset(@PathVariable("userId") Long userId, @RequestBody AssetDTO assetDTO) {
        
        Asset asset = new Asset();
        asset.setName(assetDTO.name()); 
        asset.setType(assetDTO.type());
        
        // 🌟 破案關鍵：處理 amount 為 null 的防呆機制
        Double finalAmount = assetDTO.amount();
        if (finalAmount == null) {
            // 如果前端沒傳 amount（例如股票模式下只算 totalCost），
            // 我們就自動拿 totalCost 來頂替，避免資料庫生氣報錯！
            finalAmount = assetDTO.totalCost();
        }
        asset.setAmount(finalAmount); 
        
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
    
    // 4. 修改資產 (前端 PUT)
    @PutMapping("/{id}")
    public ResponseEntity<AssetDTO> updateAsset(@PathVariable("id") Long id, @RequestBody AssetDTO assetDTO) {
        
        // 呼叫 Service 執行更新 (這就是我們剛剛在 AssetService 準備好的引擎)
        Asset updatedAsset = assetService.updateAsset(id, assetDTO);

        // 將更新後的 Entity 轉回 DTO 傳給前端
        AssetDTO updatedDTO = new AssetDTO(
            updatedAsset.getId(),
            updatedAsset.getName(),
            updatedAsset.getType(),
            updatedAsset.getAmount(),
            updatedAsset.getSymbol(),
            updatedAsset.getShares(),
            updatedAsset.getTotalCost()
        );

        return ResponseEntity.ok(updatedDTO);
    }
}