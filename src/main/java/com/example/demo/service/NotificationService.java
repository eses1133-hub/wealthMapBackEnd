package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.dto.NotificationListDTO;
import com.example.demo.entity.AlertLog;
import com.example.demo.entity.Notification;
import com.example.demo.entity.SystemNotificationRead;
import com.example.demo.repository.AlertLogRepository;
import com.example.demo.repository.NotificationReadRepository;
import com.example.demo.repository.NotificationRepository;


@Service
public class NotificationService {
	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

	@Autowired
    private NotificationRepository notificationRepository;		// 系統通知
	
	@Autowired
    private AlertLogRepository alertLogRepository;       // 個人提醒 by carly
	
	@Autowired
	private NotificationReadRepository notificationReadRepository;
	
	// 核心：用來存放 userId -> SseEmitter 的對應關係
	// 使用 ConcurrentHashMap 確保執行緒安全
	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();


	// 1. 用戶訂閱 (建立連線)
	public SseEmitter subscribe(String userId) {
		// 設定超時時間，0 表示無限 (或設定例如 30分鐘: 1800000L)
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		// 存入 Map
		emitters.put(userId, emitter);
		// 連線結束或超時時，從 Map 中移除
		emitter.onCompletion(() -> emitters.remove(userId));
		emitter.onTimeout(() -> emitters.remove(userId));
		emitter.onError((e) -> emitters.remove(userId));
		try {
			// 發送一個初始事件，確認連線成功
			emitter.send(SseEmitter.event().name("INIT").data("Connected successfully"));
		} catch (IOException e) {
			emitters.remove(userId);
		}
		return emitter;
	}

	// 2. 發送通知給特定用戶
	public void sendNotification(String userId, String message) {
		SseEmitter emitter = emitters.get(userId);

		if (emitter != null) {
			try {
				emitter.send(SseEmitter.event().name("message") // 前端監聽的事件名稱
						.data(message));
			} catch (IOException e) {
				// 發送失敗通常代表連線已斷，移除該 Emitter
				emitters.remove(userId);
			}
		}
	}
	
    // 1. 取得列表
    public List<Notification> getNotificationList() {
        return notificationRepository.findAllByOrderByScheduledDateDesc();
    }

    // 2. 儲存或更新
    public Notification saveNotification(NotificationListDTO dto) {
        Notification entity;

        if (dto.getId() != null) {
            entity = notificationRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("找不到該筆公告"));
        } else {
            entity = new Notification();
        }

        entity.setTag(dto.getTag());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());

        // 💡 關鍵就在這行！ 
        // 你的 DTO 變數叫 scheduledDate，所以這裡必須是 getScheduledDate()
        // 絕對不能出現 getScheduledTime()
        entity.setScheduledDate(dto.getScheduledDate() == null ? 
                               LocalDate.now() : dto.getScheduledDate());

        return notificationRepository.save(entity);
    }
    
    
    public void saveAlertLog(Long id, String message) {

        AlertLog entity = new AlertLog();

        entity.setId(id); // ⚠️ 前提：你的 Entity 要有這欄位
        entity.setTitle("繳款提醒");
        entity.setContent(message);
        entity.setAlertTime(LocalDateTime.now());

        alertLogRepository.save(entity);
    }

    // 3. 刪除
    public void deleteNotification(Long id) {
    	notificationRepository.deleteById(id);
    }

    public Notification findById(Long id) {
        // 使用 .orElse(null) 處理找不到資料的情況
        return notificationRepository.findById(id).orElse(null);
    }
    
    /**
     * 5. 取得系統公告未讀數 (紅點數字)
     * 邏輯：(今天以前已發布的公告總數) - (該使用者已讀的紀錄數)
     */
    public long getUnreadCount(Long userId) {
        // 💡 取得今天日期
        LocalDate today = LocalDate.now();
        
        // 算出「日期 <= 今天」的發布總數
        long total = notificationRepository.countByScheduledDateLessThanEqual(today);
        
        // 算出該用戶在已讀表中的紀錄數
        long readCount = notificationReadRepository.countByUserId(userId);
        
        // 回傳差值，Math.max 確保數字不會因為資料異常變成負數
        return Math.max(0, total - readCount);
    }
    
    // 加入個人未讀訊息的計數 by Carly
    public Map<String, Long> getDetailedUnreadCounts(Long userId) {
    	// --- 1. 系統公告邏輯 (原本的邏輯) ---
        LocalDate today = LocalDate.now();
        long systemTotal = notificationRepository.countByScheduledDateLessThanEqual(today);
        long systemRead = notificationReadRepository.countByUserId(userId);
        long systemUnread = Math.max(0, systemTotal - systemRead);
        log.info(">>> 系統公告總數 {} 筆 - 已讀公告 {} 筆 = 未讀公告 {} 筆。", systemTotal, systemRead, systemUnread);

        // --- 2. 個人訊息邏輯 (AlertLog 邏輯) ---
        // 統計該用戶 isRead 為 false 的 AlertLog
        long personalUnread = alertLogRepository.countByUser_IdAndIsReadFalseAndChannel(userId,AlertLog.NotificationChannel.WEB_PUSH);

        // --- 3. 封裝成 Map ---
        Map<String, Long> result = new HashMap<>();
        result.put("systemCount", systemUnread);
        result.put("personalCount", personalUnread);
        
        return result;
    }

    /**
     * 6. 標記系統公告為已讀
     * 當使用者點擊公告時，在 system_notification_reads 增加一筆紀錄
     */
    public void markAsRead(Long userId, Long notificationId) {
        // 1. 先檢查是否已經存在紀錄，避免重複插入重複扣數
        boolean exists  = notificationReadRepository.existsByUserIdAndNotificationId(userId, notificationId);
        if (!exists) {
            // 2. 建立新的讀取紀錄
            SystemNotificationRead read = new SystemNotificationRead();
            read.setUserId(userId);
            read.setNotificationId(notificationId);
            // 讀取時間記錄到秒沒關係，這對紅點計算沒影響
            read.setReadAt(LocalDateTime.now());
            
            // 3. 儲存
            notificationReadRepository.save(read);
        }
    }
    public List<NotificationListDTO> getNotificationListWithStatus(Long userId) {
        // 1. 抓出所有公告
//        List<Notification> allNotifications = notificationRepository.findAll();
    	// 💡 修正：改用有 OrderBy 的方法，確保「最新在前」
        List<Notification> allNotifications = notificationRepository.findAllByOrderByScheduledDateDesc();
        
        // 2. 抓出該使用者所有已讀的 ID 清單 (假設你有一個 ReadRepository)
        List<Long> readIds = notificationReadRepository.findNotificationIdsByUserId(userId);
        

        // 3. 組裝成 DTO 回傳
        return allNotifications.stream().map(n -> {
            NotificationListDTO dto = new NotificationListDTO();
            dto.setId(n.getId());
            dto.setTitle(n.getTitle());
            dto.setScheduledDate(n.getScheduledDate());
            dto.setHasRead(readIds.contains(n.getId())); 
                        return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 取得個人提醒列表 (by UserId & Channel)
     */
    public List<AlertLog> getPersonalAlerts(Long userId) {
    	// 這裡先固定為 WEB_PUSH
        AlertLog.NotificationChannel channel = AlertLog.NotificationChannel.WEB_PUSH;
        // 調用 Repository 查詢
        return alertLogRepository.findByUser_IdAndChannelOrderByAlertTimeDesc(userId, channel);
    }

}
