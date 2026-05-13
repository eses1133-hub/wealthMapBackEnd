package com.example.demo.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.dto.StrategyDTO;
import com.example.demo.entity.AlertLog;
import com.example.demo.entity.Debt;
import com.example.demo.entity.Liability;
import com.example.demo.entity.StrategySetting;
import com.example.demo.repository.AlertLogRepository;
import com.example.demo.repository.DebtRepository;
import com.example.demo.repository.LiabilityRepository;
import com.example.demo.service.DebtService;
import com.example.demo.service.EmailService;
import com.example.demo.service.LiabilityService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.SseService;

/*
 * 個人繳款通知
 */

@Component
public class DebtScheduler {
	// 1. 修正 Logger：確保類別名稱與目前的 Service 一致
		private static final Logger log = LoggerFactory.getLogger(DebtScheduler.class);

	@Autowired
	private DebtRepository debtRepository;

	@Autowired
	private DebtService debtService;

	@Autowired
	private SseService sseService;

	// 注入 SSE 服務
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private EmailService emailService;

	@Autowired
    private AlertLogRepository alertLogRepository; 
	
	@Autowired
    private LiabilityRepository liabilityRepository;

    @Autowired
    private LiabilityService liabilityService;

	@Scheduled(fixedRate = 1000000) // 每10秒跑一次(測試用)
	public void checkDueDebts() {

		int today = LocalDate.now().getDayOfMonth();

		// 查詢：匹配今日日期且開啟通知的負債項目
        List<Liability> liabilities = liabilityRepository.findByDueDayAndNotifyEnabled(today, true);

		System.out.println("【系統檢查】今天要提醒的人數：" + liabilities.size());

		for (Liability liability : liabilities) {

			double months = liabilityService.calculateRemainingMonths(liability);

            String message = String.format("【繳款提醒】您的「%s」本月需繳款，預計剩餘 %d 個月可還清。", 
                                            liability.getName(), (int) months);

            // 執行兩種通知管道，內部會各自判斷今日是否已發送過
            // 1. 即時推播 (SSE)
            executeSseNotification(liability, message);
         // 2. 發送郵件
            executeEmailNotification(liability, message);
            
			// 從 User 物件中獲取必要的資訊
//            Long userId = liability.getUser().getId();
//            String userEmail = liability.getUser().getEmail(); // 建議從 User 抓，而非寫死
//            String message = String.format("【繳款提醒】\n您的「%s」本月需繳款，預計剩餘 %d 個月可還清。", 
//                                            liability.getName(), (int) months);

//			System.out.println("發送給 userId: " + userId + " | 訊息: " + message);
	
		}
	}
	
	
	 /**
     * 執行繳款通知的 Email 流程
     */
    private void executeEmailNotification(Liability liability, String message) {
    	Long userId = liability.getUser().getId();
        String targetId = String.valueOf(liability.getId()); // 以 Liability ID 作為目標 ID

        // 檢查今天該項目的 EMAIL 是否發送過
        boolean alreadyNotified = alertLogRepository.existsByUserIdAndTargetIdAndCategoryAndChannelAndAlertTimeAfter(
                userId, 
                targetId, 
                AlertLog.AlertCategory.SYSTEM_NOTICE, 
                AlertLog.NotificationChannel.EMAIL, 
                LocalDate.now().atStartOfDay()
        );

        if (alreadyNotified) {
            log.info(">>> 用戶 {} 的項目 {} 今日已寄送過 Email，跳過。", userId, liability.getName());
            return;
        }

        AlertLog pendingLog = createBaseLog(liability, message, AlertLog.NotificationChannel.EMAIL);
        AlertLog savedLog = alertLogRepository.save(pendingLog);

        try {
            emailService.sendSimpleEmail(liability.getUser().getEmail(), "【WealthMap】個人繳款通知", message);
            savedLog.setStatus(AlertLog.AlertStatus.SENT);
            alertLogRepository.save(savedLog);
            log.info(">>> Email 寄送成功: {}", liability.getName());
        } catch (Exception e) {
            savedLog.setStatus(AlertLog.AlertStatus.FAILED);
            alertLogRepository.save(savedLog);
            log.error(">>> Email 寄送失敗! 項目: {}, 原因: {}", liability.getName(), e.getMessage());
        }
    }
    
    
    /**
     * 改寫：執行繳款通知的 SSE 流程 (含今日去重邏輯)
     */
    private void executeSseNotification(Liability liability, String message) {
        Long userId = liability.getUser().getId();
        String targetId = String.valueOf(liability.getId());

        // 檢查今天該項目的 WEB_PUSH 是否發送過
        boolean alreadyNotified = alertLogRepository.existsByUserIdAndTargetIdAndCategoryAndChannelAndAlertTimeAfter(
                userId, 
                targetId, 
                AlertLog.AlertCategory.SYSTEM_NOTICE, 
                AlertLog.NotificationChannel.WEB_PUSH, 
                LocalDate.now().atStartOfDay()
        );

        if (alreadyNotified) {
            log.info(">>> 用戶 {} 的項目 {} 今日已推播過 SSE，跳過。", userId, liability.getName());
            return;
        }

        AlertLog pendingLog = createBaseLog(liability, message, AlertLog.NotificationChannel.WEB_PUSH);
        AlertLog savedLog = alertLogRepository.save(pendingLog);

        try {
            notificationService.sendNotification(String.valueOf(userId), message);
            savedLog.setStatus(AlertLog.AlertStatus.SENT);
            alertLogRepository.save(savedLog);
            log.info(">>> SSE 推播成功: {}", liability.getName());
        } catch (Exception e) {
            savedLog.setStatus(AlertLog.AlertStatus.FAILED);
            savedLog.setErrorMessage("SSE 發送異常: " + e.getMessage());
            alertLogRepository.save(savedLog);
            log.error(">>> SSE 推播失敗: {}", e.getMessage());
        }
    }

    /**
     * Log 建立邏輯
     */
    private AlertLog createBaseLog(Liability liability, String message, AlertLog.NotificationChannel channel) {
        AlertLog log = new AlertLog();
        log.setUser(liability.getUser());
        log.setTargetId(String.valueOf(liability.getId())); // 記錄是哪一筆負債觸發的
        log.setCategory(AlertLog.AlertCategory.SYSTEM_NOTICE);
        log.setTitle("【繳款通知】" + liability.getName() + "繳款提醒");
        log.setContent(message);
        log.setChannel(channel);
        log.setStatus(AlertLog.AlertStatus.PENDING);
        log.setAlertTime(LocalDateTime.now());
        log.setRead(false);
        return log;
    }

}
