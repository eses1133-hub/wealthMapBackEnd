package com.example.demo.service;
import com.example.demo.dto.FinMindResponseDTO;
import com.example.demo.dto.StockDataDTO;
import com.example.demo.dto.StrategyDTO;
import com.example.demo.entity.AlertLog;
import com.example.demo.entity.AlertLog.AlertCategory;
import com.example.demo.entity.StockPrice;
import com.example.demo.entity.StrategySetting;
import com.example.demo.repository.AlertLogRepository;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.StockPriceRepository;
import com.example.demo.repository.StrategySettingRepository;
import com.example.demo.vo.AppResponse;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//負責「搬運」。它只管從網路（FinMind）抓資料，然後存進資料庫。它不需要知道什麼是 MA20。

@Service
@EnableScheduling
public class StockService {
	// 1. 修正 Logger：確保類別名稱與目前的 Service 一致
	private static final Logger log = LoggerFactory.getLogger(StockService.class);

	private final RestTemplate restTemplate = new RestTemplate();
	
	// 1. 使用 @Value 注入 properties 中的值
    @Value("${finmind.api.token}")
    private String apiToken;
	// 建議：將 Token 放在配置文件中，這裡示範直接定義
//	private final String API_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkYXRlIjoiMjAyNi0wMy0xOSAxNzoyNToxNyIsInVzZXJfaWQiOiJlc2VzMTExMyIsImVtYWlsIjoiamluZ3lpODYxMjExQGdtYWlsLmNvbSIsImlwIjoiMjAzLjY5LjkxLjE3MiJ9.0JW_6Chy6XJB28WAgiLZoDmUsY9ChjS9hA5dy0QYgvw";

	@Autowired
    private AssetRepository assetRepository;
	
	@Autowired
    private StockPriceRepository stockPriceRepository; // 注入 Repository
	
	@Autowired
	private StrategyService strategyService;
	
	@Autowired
	private StrategySettingRepository strategySettingRepository;
	
	@Autowired
    private AlertLogRepository alertLogRepository; 
	
	@Autowired
    private EmailService emailService;
	
	// 注入 SSE 服務
	@Autowired
	private NotificationService notificationService;
	
	/**
	 * 核心業務邏輯：執行 API 抓取 此方法為 Public，可供 Controller 手動呼叫，也可供 @Scheduled 自動呼叫
	 */
	public void executeFetch() {
		// 1. 先從 assets 表中撈出所有需要抓取的股票代號
		List<String> symbols = assetRepository.findDistinctStockSymbols();
		
		if (symbols.isEmpty()) {
            log.warn("目前資產庫中沒有任何股票(type='stock')，跳過任務。");
            return;
        }

        log.info("偵測到需更新的股票清單: {}", symbols);
        
		// --- A. 動態日期處理 ---
		// 使用 Java 8 LocalDate 自動取得執行當下的日期
        String today = LocalDate.now().toString();// 格式為 yyyy-MM-dd
        String startDate = LocalDate.now().minusDays(45).toString(); // 抓取過去 14 天的資料
        log.info("開始執行股票資料抓取任務，區間：{} 至 {}", startDate, today);
        
        // 2. 針對每個代號進行 API 呼叫
        symbols.forEach(symbol -> {
            try {
            	fetchAndSaveBySymbol(symbol);
//                fetchAndSaveBySymbol(symbol, startDate, today);
            } catch (Exception e) {
                log.error("抓取股票 {} 時發生錯誤: {}", symbol, e.getMessage());
            }
        });
		
	}
	private void fetchAndSaveBySymbol(String symbol) {
//	private void fetchAndSaveBySymbol(String symbol, String startDate, String endDate) {
		// 判斷資料庫存量
	    long count = stockPriceRepository.countBySymbol(symbol);
	    
	    String today = LocalDate.now().toString();
	    String startDate;
	    
	    if (count < 20) {
	        // --- 情況 A：冷啟動模式 (資料不足 20 筆) ---
	        // 抓 45 天是為了確保包含週末與節日後，能湊齊 20 個交易日
	        startDate = LocalDate.now().minusDays(45).toString();
	        log.info(">>> 股票 {} 歷史資料不足 ({} 筆)，執行完整抓取模式。", symbol, count);
	    } else {
	        // --- 情況 B：增量更新模式 (已有基礎資料) ---
	        // 建議抓「過去 5 天」而不是 1 天，這是為了防止連假、颱風假或系統排程斷掉
	        startDate = LocalDate.now().minusDays(5).toString();
	        log.info(">>> 股票 {} 已有基礎資料，執行增量更新模式。", symbol);
	    }
	    
	    // API 呼叫邏輯不變，使用動態產生的 startDate
	    executeRealApiCall(symbol, startDate, today);
		
	}
	
	/**
     * 封裝後的 executeRealApiCall
     * 現在它的職責是：1. 設定API，並呼叫API  2. 存入資料
     */
	private void executeRealApiCall(String symbol, String startDate, String endDate) {
		
		// --- B. 設定請求標頭 (Headers) ---
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + apiToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		// --- C. 動態建構 URL ---
		String url = UriComponentsBuilder.fromUriString("https://api.finmindtrade.com/api/v4/data")
				.queryParam("dataset", "TaiwanStockPrice").queryParam("data_id", symbol)
				.queryParam("start_date", startDate).queryParam("end_date", endDate).toUriString();
		try {
			// --- D. 發送請求 ---
			ResponseEntity<FinMindResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					FinMindResponseDTO.class);
			// --- E. 資料解析與防呆機制 ---
			FinMindResponseDTO body = response.getBody();
			if (body != null && "success".equals(body.getMsg()) && body.getData() != null) {

				List<StockDataDTO> stockList = body.getData();
				// 檢查是否有回傳資料，避免存取 index 0 時噴錯
				if (!stockList.isEmpty()) {
					log.info(">>> 授權呼叫成功！取得 {} 筆資料", stockList.size());
					log.info("--- 股票代號: {} 歷史行情 ---", stockList.get(0).getStockId());

					// 整理並印出開高低收
					stockList.forEach(s -> {
						log.info("日期: {} | 開盤: {} | 最高: {} | 最低: {} | 收盤: {}", s.getDate(), s.getOpen(), s.getMax(),
								s.getMin(), s.getClose());
					});
					
					// 將 DTO 轉換為 Entity，並過濾掉資料庫已存在的日期
					List<StockPrice> newStockPrice = stockList.stream().map(s -> {
						StockPrice stock = new StockPrice();
						stock.setSymbol(s.getStockId());
						String dateStr = s.getDate();
						LocalDate date = LocalDate.parse(dateStr);
						stock.setDate(date);
						stock.setClosePrice(s.getClose());
						stock.setHighPrice(s.getMax());
						return stock;

					}).filter(stock -> !stockPriceRepository.existsBySymbolAndDate(stock.getSymbol(), stock.getDate()))
					  .collect(Collectors.toList());

					// 執行批量存檔
					if (!newStockPrice.isEmpty()) {
						stockPriceRepository.saveAll(newStockPrice);
						log.info(">>> 成功存入 {} 筆新資料！", newStockPrice.size());
						

					} else {
						log.info(">>> 資料已存在，本次無須更新。");
					}
					// 資料存完後，觸發計算乖離率與通知
				    this.checkAndNotifyStrategy(symbol);
		            // 若需要跑過去20筆資料，可先將上方function註解
				} else {
					log.warn("API 請求成功，但該時段內無交易資料（可能為非交易日）。");
				}
			}

		} catch (Exception e) {
			log.error("API 呼叫過程中發生異常: {}", e.getMessage());
		}
	}
	
	
	/**
     * 修改後的 checkAndNotifyStrategy
     * 現在它的職責是：1. 算好乖離率存入資料庫 2. 觸發警報判斷
     */
    public void checkAndNotifyStrategy(String symbol) {
        try {
            // Step 1: 計算並將乖離率紀錄回 StockPrice 表
            StrategyDTO currentData = updateStockBias(symbol);
            
            if (currentData == null) return;

            // Step 2: 處理所有訂閱此股票的使用者通知邏輯
            processUserAlerts(symbol, currentData);

        } catch (Exception e) {
            log.error(">>> 處理股票 {} 的策略更新時發生異常: {}", symbol, e.getMessage());
        }
    }
	
    
    /**
     * 功能 A: 計算乖離率並存回資料庫
     * 這樣網頁之後直接讀取 StockPrice 就能拿到 bias，不用重算。
     */
    private StrategyDTO updateStockBias(String symbol) {
        // 呼叫 StrategyService 計算當前乖離率 (門檻給 0 因為只是要拿數值)
        StrategyDTO result = strategyService.calculateMa20Strategy(symbol, 0.0, 0.0);
        
        if (result != null) {
        	// 找出資料庫中最近的一筆紀錄
        	stockPriceRepository.findFirstBySymbolOrderByDateDesc(symbol)
            .ifPresent(latestPrice -> {
            	// 將乖離率四捨五入到小數點後四位 (因為 0.0123 代表 1.23%)
                double roundedBias = BigDecimal.valueOf(result.getBias())
                    .setScale(4, RoundingMode.HALF_UP)
                    .doubleValue();
                latestPrice.setBias(roundedBias); // 存入乖離率
                stockPriceRepository.save(latestPrice);
                log.info(">>> 股票 {} [日期:{}] 乖離率已更新: {}%", 
                    symbol, latestPrice.getDate(), roundedBias * 100);
            });
            
        }
        return result;
    }

	/**
	 * 功能 B: 判斷是否符合寄送標準並執行寄送
	 */
	private void processUserAlerts(String symbol, StrategyDTO currentData) {
		// 1. 找出所有訂閱這支股票且啟用的設定
		List<StrategySetting> activeSettings = strategySettingRepository.findBySymbolAndIsActiveTrue(symbol);

		if (activeSettings.isEmpty()) {
			log.info(">>> 股票 {} 目前沒有啟用的使用者設定，跳過檢查。", symbol);
			return;
		}

		for (StrategySetting setting : activeSettings) {
			// 判斷當前乖離率是否超過該使用者的自訂門檻
			// 這裡直接用剛才算好的 currentData 進行比較，不用再進 Service 算 MA20 了
			boolean shouldBuy = currentData.getBias() * 100 <= setting.getBuyThreshold();
			boolean shouldSell = currentData.getBias() * 100 >= setting.getSellThreshold();

			String action = null;
			// 優先判斷減碼 (停利通常優先級較高)
		    if (shouldSell) {
		        action = "建議減碼";
		    } 
		    // 如果沒達到減碼，再看是否達到加碼
		    else if (shouldBuy) {
		        action = "建議加碼";
		    }
		    
			// 如果達到門檻且需要觸發
		    if (action != null) {
		    	try {
				// 【執行發送】 by mail
				executeEmailNotification(setting, currentData, action);
				// by SSE/WEB_PUSH
				executeSseNotification(setting, currentData, action);
		    	}catch(Exception e) {
		    		log.error(">>> Email 寄送失敗!");
		    	}
			}
		}
	}

    /**
     * 功能 C: 執行郵件寄送 (原有的 Log 與 Email 邏輯)
     */
    private void executeEmailNotification(StrategySetting setting, StrategyDTO result, String action) {
    	log.info(">>> 進入寄信流程：User={}, Stock={}", setting.getUser().getId(), setting.getSymbol());
        // 檢查今天發過沒
		boolean alreadyNotified = alertLogRepository.existsByUserIdAndTargetIdAndCategoryAndChannelAndAlertTimeAfter(
	    	    setting.getUser().getId(), 
	    	    setting.getSymbol(), 
	    	    AlertLog.AlertCategory.STOCK_STRATEGY, 
	    	    AlertLog.NotificationChannel.EMAIL, 
	    	    LocalDate.now().atStartOfDay()
	    	);
		log.info(">>> 是否已通知過: {}", alreadyNotified);

        if (!alreadyNotified) {
        	// 【預約提醒】先存入 Log 並標記為 PENDING
            AlertLog pendingLog = new AlertLog();
            pendingLog.setUser(setting.getUser());
            pendingLog.setTargetId(setting.getSymbol());
            pendingLog.setCategory(AlertLog.AlertCategory.STOCK_STRATEGY);
            pendingLog.setTitle("【WealthMap】" + setting.getSymbol() + " 策略觸發：" + action);
            pendingLog.setContent(String.format("現價：%.2f\nMA20：%.2f\n乖離率：%.2f%%\n建議：%s", 
                result.getCurrentPrice(), result.getMa20(), result.getBias() * 100, action));
            pendingLog.setChannel(AlertLog.NotificationChannel.EMAIL);
            pendingLog.setStatus(AlertLog.AlertStatus.PENDING);
            pendingLog.setAlertTime(LocalDateTime.now());

            AlertLog savedLog = alertLogRepository.save(pendingLog);
            
            try {
            	// 2. 寄信
                emailService.sendStrategyEmail(setting.getUser().getEmail(), savedLog);
                
                // 3. 寄信成功後，將狀態改為 SENT
                savedLog.setStatus(AlertLog.AlertStatus.SENT);
                alertLogRepository.save(savedLog);
                log.info(">>> Email 寄送成功: {}", setting.getSymbol());
            }catch(Exception e){
            	// 4. 失敗時記錄原因，狀態改為 FAILED
                savedLog.setStatus(AlertLog.AlertStatus.FAILED);
                alertLogRepository.save(savedLog);
                log.error(">>> Email 寄送失敗! 股票: {}, 原因: {}", setting.getSymbol(), e.getMessage());
            }
        }else {
            log.warn(">>> 今天已寄送過，跳過流程");
        }
    }
    
    
    /**
     * 功能 D: 執行個人通知 (SSE / WEB_PUSH)
     * 仿照 Email 邏輯：先存 Log 確保不漏失，再執行推播
     */
    private void executeSseNotification(StrategySetting setting, StrategyDTO result, String action) {
    	// 1. 檢查今日是否已發過該股票的網頁通知 (避免重複洗板)
        boolean alreadyNotified = alertLogRepository.existsByUserIdAndTargetIdAndCategoryAndChannelAndAlertTimeAfter(
    	    setting.getUser().getId(), 
    	    setting.getSymbol(), 
    	    AlertLog.AlertCategory.STOCK_STRATEGY, 
    	    AlertLog.NotificationChannel.WEB_PUSH, // 👈 加上這個
    	    LocalDate.now().atStartOfDay()
    	);

        if (alreadyNotified) {
            log.info(">>> 使用者 {} 的股票 {} 今日已發過網頁通知，跳過。", setting.getUser().getId(), setting.getSymbol());
            return;
        }

        // 2. 準備 Log 紀錄 (PENDING)
        AlertLog pendingLog = new AlertLog();
        pendingLog.setUser(setting.getUser());
        pendingLog.setTargetId(setting.getSymbol());
        pendingLog.setCategory(AlertLog.AlertCategory.STOCK_STRATEGY);
        pendingLog.setTitle("【加減碼通知】" + setting.getSymbol() + " " + action);
        
        String message = String.format(
            "股票: %s \n目前價格: %.2f \n乖離率: %.2f%% \n建議: %s (門檻: %.2f)",
            setting.getSymbol(),
            result.getCurrentPrice(),
            result.getBias() * 100,
            action,
            action.equals("建議加碼") ? setting.getBuyThreshold() : setting.getSellThreshold()
        );
        pendingLog.setContent(message);
        pendingLog.setChannel(AlertLog.NotificationChannel.WEB_PUSH); // 標記為網頁通知
        pendingLog.setStatus(AlertLog.AlertStatus.PENDING);
        pendingLog.setAlertTime(LocalDateTime.now());
        pendingLog.setRead(false); // 初始為未讀

        // 先存入資料庫取得 ID
        AlertLog savedLog = alertLogRepository.save(pendingLog);

        try {
            // 3. 執行 SSE 實時推播
            String userIdStr = String.valueOf(setting.getUser().getId());
            notificationService.sendNotification(userIdStr, message);

            // 4. 推播成功，更新 Log 狀態
            savedLog.setStatus(AlertLog.AlertStatus.SENT);
            alertLogRepository.save(savedLog);
            log.info(">>> SSE 成功發送至用戶 {}: {}", userIdStr, setting.getSymbol());

        } catch (Exception e) {
            // 5. 推播失敗，記錄錯誤訊息
            savedLog.setStatus(AlertLog.AlertStatus.FAILED);
            savedLog.setErrorMessage("SSE 發送異常: " + e.getMessage());
            alertLogRepository.save(savedLog);
            log.error(">>> SSE 發送失敗: {}", e.getMessage());
        }
    	
    }
	
	/**
     * 針對特定股票，檢查所有使用者的設定
     */
    public void checkAndNotifyStrategy_old(String symbol) {
        // 1. 找出所有訂閱這支股票且啟用的設定
        List<StrategySetting> activeSettings = strategySettingRepository.findBySymbolAndIsActiveTrue(symbol);
        
        if (activeSettings.isEmpty()) {
            log.info(">>> 股票 {} 目前沒有啟用的使用者設定，跳過檢查。", symbol);
            return;
        }

        for (StrategySetting setting : activeSettings) {
            try {
                // 2. 呼叫策略 Service 計算當前乖離率
                StrategyDTO result = strategyService.calculateMa20Strategy(symbol, setting.getBuyThreshold(), setting.getSellThreshold());

                // 3. 如果達到門檻且需要觸發
                if (result != null && result.isShouldNotify()) {
                	// 3. 檢查今天發過沒
                    boolean alreadyNotified = alertLogRepository.existsByUserIdAndTargetIdAndCategoryAndAlertTimeAfter(
                        setting.getUser().getId(), symbol, AlertLog.AlertCategory.STOCK_STRATEGY, 
                        LocalDate.now().atStartOfDay());
                    if (!alreadyNotified) {
                        // 4. 【預約提醒】先存入 Log 並標記為 PENDING
                        AlertLog pendingLog = new AlertLog();
                        pendingLog.setUser(setting.getUser());
                        pendingLog.setTargetId(symbol);
                        pendingLog.setCategory(AlertLog.AlertCategory.STOCK_STRATEGY);
                        pendingLog.setTitle("【WealthMap】" + symbol + " 策略觸發：" + result.getAction());
//                        pendingLog.setContent("現價：" + result.getCurrentPrice() + "，建議：" + result.getAction());
                        // 內容可以寫得更詳細一點，方便以後在 LOG 內查看
                        pendingLog.setContent(String.format("現價：%.2f，MA20：%.2f，乖離率：%.2f%%，建議：%s", 
                                              result.getCurrentPrice(), result.getMa20(), 
                                              result.getBias() * 100, result.getAction()));
                        pendingLog.setChannel(AlertLog.NotificationChannel.EMAIL);
                        pendingLog.setStatus(AlertLog.AlertStatus.PENDING);
                        pendingLog.setAlertTime(LocalDateTime.now());

                        AlertLog savedLog = alertLogRepository.save(pendingLog);

                        // 5. 【執行發送】
                        emailService.sendStrategyEmail(setting.getUser().getEmail(), savedLog);
                    }
                }
            } catch (Exception e) {
                log.error(">>> 處理使用者 {} 的股票 {} 策略時發生錯誤: {}", 
                          setting.getUser().getId(), symbol, e.getMessage());
            }
        }
    }

    
	/**
	 * 排程觸發點 設定：每週一至週五，下午 14:00 執行 (台股收盤後且資料更新後) 指定時區：Asia/Taipei 確保在雲端環境也能準時執行
	 */
	@Scheduled(cron = "0 30 14 * * MON-FRI", zone = "Asia/Taipei")
	public void scheduledTask() {
		log.info("=== 定時排程啟動 ===");
		executeFetch();
	}
	
	/**
	 * 專供前端彈窗呼叫：取得特定股票的現價與乖離率
	 */
	public StrategyDTO getQuickQuote(String symbol) {
		try {
	        // 1. 嘗試從資料庫拿現成的（最快）
	        Optional<StockPrice> latestOpt = stockPriceRepository.findFirstBySymbolOrderByDateDesc(symbol);

	        if (latestOpt.isPresent() && latestOpt.get().getBias() != null) {
	            return convertToDTO(latestOpt.get());
	        }

	        // 2. 【死結排除】如果資料庫沒資料，或是資料還沒算過 bias
	        log.info(">>> 偵測到新股票或資料不完整: {}，啟動即時補課...", symbol);
	        
	        // 呼叫我們之前封裝好的「模式判斷」抓取邏輯
	        // 這會去 FinMind 抓 45 天資料並存入資料庫
	        this.fetchAndSaveBySymbol(symbol); 

	        // 3. 抓完存好後，現在資料庫有資料了，再叫 Service 算一次
	        return strategyService.calculateMa20Strategy(symbol, 0.0, 0.0);

	    } catch (Exception e) {
	        log.error("快速取得報價失敗 ({}): {}", symbol, e.getMessage());
	        return null;
	    }
	}
	
	// 輔助方法：將 Entity 轉為 DTO
	private StrategyDTO convertToDTO(StockPrice latest) {
	    StrategyDTO dto = new StrategyDTO();
	    dto.setSymbol(latest.getSymbol());
	    dto.setCurrentPrice(latest.getClosePrice());
	    dto.setBias(latest.getBias());
	    dto.setDate(latest.getDate());	    
	    return dto;
	}
	
}
