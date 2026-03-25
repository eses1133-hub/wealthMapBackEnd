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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//負責「搬運」。它只管從網路（FinMind）抓資料，然後存進資料庫。它不需要知道什麼是 MA20。

@Service
@EnableScheduling
public class StockService {
	// 1. 修正 Logger：確保類別名稱與目前的 Service 一致
	private static final Logger log = LoggerFactory.getLogger(StockService.class);

	private final RestTemplate restTemplate = new RestTemplate();
	// 建議：將 Token 放在配置文件中，這裡示範直接定義
	private final String API_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkYXRlIjoiMjAyNi0wMy0xOSAxNzoyNToxNyIsInVzZXJfaWQiOiJlc2VzMTExMyIsImVtYWlsIjoiamluZ3lpODYxMjExQGdtYWlsLmNvbSIsImlwIjoiMjAzLjY5LjkxLjE3MiJ9.0JW_6Chy6XJB28WAgiLZoDmUsY9ChjS9hA5dy0QYgvw";

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
                fetchAndSaveBySymbol(symbol, startDate, today);
            } catch (Exception e) {
                log.error("抓取股票 {} 時發生錯誤: {}", symbol, e.getMessage());
            }
        });
		
	}
	
	private void fetchAndSaveBySymbol(String symbol, String startDate, String endDate) {
		// --- B. 設定請求標頭 (Headers) ---
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + API_TOKEN);
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

					if (!newStockPrice.isEmpty()) {
						stockPriceRepository.saveAll(newStockPrice);
						log.info(">>> 成功存入 {} 筆新資料！", newStockPrice.size());
						
						// --- 呼叫封裝好的檢查機制 ---
			            this.checkAndNotifyStrategy(symbol);
					} else {
						log.info(">>> 資料已存在，本次無須更新。");
					}
				} else {
					log.warn("API 請求成功，但該時段內無交易資料（可能為非交易日）。");
				}
			}

		} catch (Exception e) {
			log.error("API 呼叫過程中發生異常: {}", e.getMessage());
		}
	}
	
	
	/**
     * 針對特定股票，檢查所有使用者的設定
     */
    public void checkAndNotifyStrategy(String symbol) {
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
	@Scheduled(cron = "0 00 14 * * MON-FRI", zone = "Asia/Taipei")
	public void scheduledTask() {
		log.info("=== 定時排程啟動 ===");
		executeFetch();
	}
	
	/**
	 * 專供前端彈窗呼叫：取得特定股票的現價與乖離率
	 */
	public StrategyDTO getQuickQuote(String symbol) {
	    // 1. 確保資料庫裡有最新的資料 (或者直接呼叫 fetch 抓一次最新的)
	    // 這裡我們先假設資料庫已有資料，直接計算
	    
	    // 門檻值暫設為 0，因為我們現在只想拿價格跟乖離率，不需觸發通知
	    try {
	        return strategyService.calculateMa20Strategy(symbol, 0.0, 0.0);
	    } catch (Exception e) {
	        log.error("快速取得報價失敗: {}", e.getMessage());
	        return null;
	    }
	}
	
}
